$port = 8080
Write-Host "Checking for process on port $port..."

$connections = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue

if ($connections) {
    $processId = $connections | Select-Object -ExpandProperty OwningProcess -Unique
    if ($processId) {
        Write-Host "Found process ID $processId listening on port $port. Killing it..."
        Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
        Write-Host "Process $processId killed."
    }
} else {
    Write-Host "No process found on port $port. Ready to launch."
}
