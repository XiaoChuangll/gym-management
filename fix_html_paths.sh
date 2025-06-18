#!/bin/bash

# 修复所有使用默认布局的HTML文件中的CSS和JS引用路径
find src/main/resources/templates -name "*.html" -type f -exec grep -l "layout:decorate=\"~{layout/default}\"" {} \; | while read file; do
  echo "Processing $file"
  # 移除重复的CSS和JS引用
  sed -i '/<link rel="stylesheet" href="https:\/\/cdn.jsdelivr.net\/npm\/bootstrap/d' "$file"
  sed -i '/<link rel="stylesheet" href="https:\/\/cdn.jsdelivr.net\/npm\/bootstrap-icons/d' "$file"
  sed -i '/<link rel="stylesheet" th:href="@{\/static\/css\/style.css}">/d' "$file"
  sed -i '/<script src="https:\/\/cdn.jsdelivr.net\/npm\/jquery/d' "$file"
  sed -i '/<script src="https:\/\/cdn.jsdelivr.net\/npm\/bootstrap/d' "$file"
  sed -i '/<script src="https:\/\/cdn.jsdelivr.net\/npm\/chart.js/d' "$file"
done

# 修复不使用默认布局的HTML文件中的CSS和JS引用路径
find src/main/resources/templates -name "*.html" -type f -exec grep -L "layout:decorate=\"~{layout/default}\"" {} \; | while read file; do
  echo "Processing $file"
  # 修复CSS和JS引用路径
  sed -i 's/th:href="@{\/static\/css\/style.css}"/th:href="@{\/css\/style.css}"/g' "$file"
  sed -i 's/th:src="@{\/static\/js\/main.js}"/th:src="@{\/js\/main.js}"/g' "$file"
  sed -i 's/th:src="@{\/static\/js\/charts.js}"/th:src="@{\/js\/charts.js}"/g' "$file"
  sed -i 's/th:src="@{\/static\/img/th:src="@{\/img/g' "$file"
done

echo "All HTML files have been processed." 