# 修复所有使用默认布局的HTML文件中的CSS和JS引用路径
$layoutFiles = Get-ChildItem -Path "src\main\resources\templates" -Filter "*.html" -Recurse | 
    Where-Object { $_ | Select-String -Pattern 'layout:decorate="~{layout/default}"' -Quiet }

foreach ($file in $layoutFiles) {
    Write-Host "Processing $($file.FullName)"
    $content = Get-Content -Path $file.FullName -Raw
    
    # 移除重复的CSS和JS引用
    $content = $content -replace '<link rel="stylesheet" href="https://cdn\.jsdelivr\.net/npm/bootstrap[^>]*>', ''
    $content = $content -replace '<link rel="stylesheet" href="https://cdn\.jsdelivr\.net/npm/bootstrap-icons[^>]*>', ''
    $content = $content -replace '<link rel="stylesheet" th:href="@{/static/css/style\.css}">', ''
    $content = $content -replace '<script src="https://cdn\.jsdelivr\.net/npm/jquery[^>]*></script>', ''
    $content = $content -replace '<script src="https://cdn\.jsdelivr\.net/npm/bootstrap[^>]*></script>', ''
    $content = $content -replace '<script src="https://cdn\.jsdelivr\.net/npm/chart\.js[^>]*></script>', ''
    
    Set-Content -Path $file.FullName -Value $content
}

# 修复不使用默认布局的HTML文件中的CSS和JS引用路径
$nonLayoutFiles = Get-ChildItem -Path "src\main\resources\templates" -Filter "*.html" -Recurse | 
    Where-Object { -not ($_ | Select-String -Pattern 'layout:decorate="~{layout/default}"' -Quiet) }

foreach ($file in $nonLayoutFiles) {
    Write-Host "Processing $($file.FullName)"
    $content = Get-Content -Path $file.FullName -Raw
    
    # 修复CSS和JS引用路径
    $content = $content -replace 'th:href="@{/static/css/style\.css}"', 'th:href="@{/css/style.css}"'
    $content = $content -replace 'th:src="@{/static/js/main\.js}"', 'th:src="@{/js/main.js}"'
    $content = $content -replace 'th:src="@{/static/js/charts\.js}"', 'th:src="@{/js/charts.js}"'
    $content = $content -replace 'th:src="@{/static/img/', 'th:src="@{/img/'
    
    Set-Content -Path $file.FullName -Value $content
}

Write-Host "All HTML files have been processed." 