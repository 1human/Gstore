<#
    Prunes the packaged desktop image.

    jpackage copies every runtime jar whole, so the Windows image carries the SQLite and JNA
    natives for macOS/Linux plus the entire 36 MB icon jar whose classes the app barely touches.
    This drops the entries the app cannot reach; nothing is renamed, optimised or rewritten.

    Usage: prune-desktop-image.ps1 <app-image-root> [<app-image-root> ...]
#>
param(
    [string]$JdkHome = '',
    [string]$Modules = '',
    [Parameter(ValueFromRemainingArguments = $true)][string[]]$Roots
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem

$iconPattern = 'androidx/compose/material/icons/(?:automirrored/)?(?:filled|outlined|rounded|sharp|twotone)/[A-Za-z0-9_]+'
$latin1 = [System.Text.Encoding]::GetEncoding('ISO-8859-1')

function Rewrite-Jar([string]$Path, [scriptblock]$Keep) {
    $tmp = "$Path.pruned"
    $source = [System.IO.Compression.ZipFile]::OpenRead($Path)
    $stream = [System.IO.File]::Create($tmp)
    $target = New-Object System.IO.Compression.ZipArchive($stream, [System.IO.Compression.ZipArchiveMode]::Create)
    try {
        foreach ($entry in $source.Entries) {
            if (-not $Keep.Invoke($entry.FullName)) { continue }
            $copy = $target.CreateEntry($entry.FullName, [System.IO.Compression.CompressionLevel]::Optimal)
            $input = $entry.Open()
            $output = $copy.Open()
            try { $input.CopyTo($output) } finally { $output.Dispose(); $input.Dispose() }
        }
    } finally {
        $target.Dispose(); $stream.Dispose(); $source.Dispose()
    }
    Remove-Item -LiteralPath $Path -Force
    Move-Item -LiteralPath $tmp -Destination $Path
}

# jpackage builds the bundled runtime with plain jlink, so the module image is stored
# uncompressed. Rebuild it with compression: same modules, roughly half the space.
function Compress-Runtime([string]$Root) {
    $runtime = Join-Path $Root 'runtime'
    if (-not (Test-Path -LiteralPath $runtime)) { return }
    if ([string]::IsNullOrWhiteSpace($JdkHome) -or [string]::IsNullOrWhiteSpace($Modules)) { return }

    $jlink = Join-Path $JdkHome 'bin\jlink.exe'
    if (-not (Test-Path -LiteralPath $jlink)) {
        Write-Warning "jlink not found in $JdkHome; leaving the runtime uncompressed"
        return
    }

    $output = "$runtime.compressed"
    if (Test-Path -LiteralPath $output) { Remove-Item -LiteralPath $output -Recurse -Force }

    & $jlink --module-path (Join-Path $JdkHome 'jmods') --add-modules $Modules `
        --compress=zip-6 --strip-debug --no-header-files --no-man-pages --output $output 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) {
        # Older JDKs only know the numeric form.
        Remove-Item -LiteralPath $output -Recurse -Force -ErrorAction SilentlyContinue
        & $jlink --module-path (Join-Path $JdkHome 'jmods') --add-modules $Modules `
            --compress=2 --strip-debug --no-header-files --no-man-pages --output $output 2>&1 | Out-Null
    }
    if ($LASTEXITCODE -ne 0 -or -not (Test-Path -LiteralPath (Join-Path $output 'bin\java.exe'))) {
        Write-Warning 'jlink compression failed; keeping the runtime jpackage produced'
        Remove-Item -LiteralPath $output -Recurse -Force -ErrorAction SilentlyContinue
        return
    }

    Remove-Item -LiteralPath $runtime -Recurse -Force
    Move-Item -LiteralPath $output -Destination $runtime
}

foreach ($root in $Roots) {
    $appDir = Join-Path $root 'app'
    if (-not (Test-Path -LiteralPath $appDir)) { continue }

    $jars = Get-ChildItem -LiteralPath $appDir -Filter '*.jar'
    $iconsJar = $jars | Where-Object { $_.Name -like 'material-icons-extended*' } | Select-Object -First 1

    $used = New-Object 'System.Collections.Generic.HashSet[string]'
    if ($iconsJar) {
        foreach ($jar in $jars) {
            if ($jar.FullName -eq $iconsJar.FullName) { continue }
            $zip = [System.IO.Compression.ZipFile]::OpenRead($jar.FullName)
            try {
                foreach ($entry in $zip.Entries) {
                    if (-not $entry.FullName.EndsWith('.class')) { continue }
                    $stream = $entry.Open()
                    $buffer = New-Object System.IO.MemoryStream
                    try { $stream.CopyTo($buffer) } finally { $stream.Dispose() }
                    $text = $latin1.GetString($buffer.ToArray())
                    $buffer.Dispose()
                    foreach ($match in [regex]::Matches($text, $iconPattern)) { [void]$used.Add($match.Value) }
                }
            } finally { $zip.Dispose() }
        }
    }

    foreach ($jar in $jars) {
        $isIconsJar = $iconsJar -and $jar.FullName -eq $iconsJar.FullName
        Rewrite-Jar $jar.FullName {
            param($name)
            # com/sun/jna/win32/ holds classes; only the <platform>/ directories hold natives.
            $jnaPlatform = if ($name -match '^com/sun/jna/([^/]+)/') { $Matches[1] } else { '' }
            if ($name.StartsWith('natives/')) { return $name.StartsWith('natives/windows_x64/') }
            if ($jnaPlatform -match '^(aix|darwin|freebsd|linux|openbsd|sunos|w32ce|win32-aarch64|win32-x86)') {
                return $false
            }
            if ($isIconsJar -and $name.StartsWith('androidx/compose/material/icons/')) {
                $className = $name -replace '\.class$', ''
                if ($className.Contains('$')) { $className = $className.Substring(0, $className.IndexOf('$')) }
                return $used.Contains($className)
            }
            return $true
        }
    }

    Compress-Runtime -Root $root

    $size = (Get-ChildItem -LiteralPath $root -Recurse -File | Measure-Object Length -Sum).Sum
    Write-Host ("pruned {0}: {1:N1} MB" -f $root, ($size / 1MB))
}
