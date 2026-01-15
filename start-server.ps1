# RAIG-Java Web Server Startup Script
# Run this script to start the web interface

Write-Host "`n🚀 RAIG-JAVA WEB SERVER STARTUP" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# Set Java 23
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Navigate to project directory
$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectDir

Write-Host "`n📦 Compiling project..." -ForegroundColor Yellow
mvn compile -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation successful!" -ForegroundColor Green
    
    Write-Host "`n🌐 Starting web server..." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host "  WEB INTERFACE: http://localhost:8080" -ForegroundColor White -BackgroundColor DarkGreen
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "📖 Open your browser and navigate to: http://localhost:8080" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Available endpoints:" -ForegroundColor Yellow
    Write-Host "  • GET  /               - Web Interface (Homepage)" -ForegroundColor Gray
    Write-Host "  • GET  /api/pillars    - List 7 AI Ethics Pillars" -ForegroundColor Gray
    Write-Host "  • GET  /api/scenarios  - Get Demo Scenarios" -ForegroundColor Gray
    Write-Host "  • POST /api/evaluate   - Evaluate AI Decision" -ForegroundColor Gray
    Write-Host ""
    Write-Host "⏹️  Press Ctrl+C to stop the server" -ForegroundColor Red
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host ""
    
    # Run the server
    java -cp "target/classes;$HOME\.m2\repository\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar;$HOME\.m2\repository\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar;$HOME\.m2\repository\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar" Main
    
} else {
    Write-Host "`n❌ Compilation failed! Please check for errors above." -ForegroundColor Red
    Write-Host "💡 Try running: mvn clean compile" -ForegroundColor Yellow
}

Write-Host "`n👋 Server stopped." -ForegroundColor Yellow
