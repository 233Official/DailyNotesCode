# 设置一个示例任务的总步数
$totalSteps = 100

# 循环遍历每一步
for ($step = 1; $step -le $totalSteps; $step++) {
    # 计算完成的百分比
    $percentComplete = ($step / $totalSteps) * 100

    # 使用 Write-Progress 显示进度条
    Write-Progress -Activity "处理中" -Status "$step out of $totalSteps completed" -PercentComplete $percentComplete

    # 模拟长时间运行的任务
    Start-Sleep -Milliseconds 100
}

# 循环结束后，显示完成信息
Write-Host "任务完成！"
