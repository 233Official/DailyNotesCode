#!/bin/bash

# WAL日志目录的路径
WAL_DIR="/var/lib/pgsql/15/data/pg_wal"

# waldump 程序绝对路径
WAL_DUMP="/usr/pgsql-15/bin/pg_waldump"

# 输出文件的基础名
OUTPUT_BASE="output"

# 分割行数
LINES_PER_PART=100000

# 指定日期范围
START_DATE="2023-12-18"
END_DATE="2023-12-22"

# WAL文件的正则表达式模式
WAL_PATTERN='^[0-9A-F]{24}$'

# 跳过标识 break_flag, 默认为 false
break_flag=false
echo "当前 break_flag：$break_flag"

# 遍历目录下的所有文件
for WAL_FILE in "$WAL_DIR"/*
do
    FILE_NAME=$(basename "$WAL_FILE")
    echo "正在处理文件：$FILE_NAME"

    # 检查文件名是否符合WAL日志的命名模式
    if [[ $FILE_NAME =~ $WAL_PATTERN ]]; then
        # 每 100000 行截取一次日志, 迭代器:
        iter_num=0

        # 在 while 循环中，每次迭代截取 100000 行日志
        while true
        do
            # pass
            echo "当前迭代器：$iter_num"
            iter_num=$((iter_num+1))
            # 由于某个条件 break，跳出循环
            if [[ $iter_num > 10 ]]; then
                echo "当前迭代器：$iter_num, 大于 10，跳出循环"
                break
            fi
        done


        # 拼接文件名
        end_line=$(( ($iter_num + 1) * 100000 ))
        iter_file_name="${FILE_NAME}_head_${iter_num}-$end_line.log"
        # 截取前 100000 行 WAL_DIR WAL 日志输出到 $FILE_NAME_head_{iter_num}-{iter_num+1}*100000.log
        $WAL_DUMP "$WAL_FILE" | head -n 100000 > "$iter_file_name"
        echo "截取前 100000 行 WAL 日志输出到 $iter_file_name"
        iter_num=$((iter_num+1))
        echo "当前迭代器：$iter_num"
        break_flag=true
        # 截取 iter_num 行到 iter_num+1*100000 行 WAL_DIR WAL 日志输出到 $FILE_NAME_head_{iter_num}-{iter_num+1}*100000.log
        # $WAL_DUMP "$WAL_FILE" | head -n $((iter_num*100000)) | tail -n 100000 > "${FILE_NAME}_head_${iter_num}-$((iter_num+1))*100000.log"

        # # 截取前 100 行 WAL_DIR WAL 日志输出到 $FILE_NAME_head100000.log
        # $WAL_DUMP "$WAL_FILE" | head -n 100 > "${FILE_NAME}_head100.log"
        # echo "截取前 100 行 WAL 日志输出到 ${FILE_NAME}_head100.log"

        # # 匹配前 100 行中第一次出现的日期
        # first_date_in_this_file=$(grep -m 1 -E "COMMIT [0-9]{4}-[0-9]{2}-[0-9]{2}" "${FILE_NAME}_head100.log" | grep -o -E "[0-9]{4}-[0-9]{2}-[0-9]{2}")
        # echo "第一次出现的日期：$first_date_in_this_file"
        # # 清除临时文件
        # rm "${FILE_NAME}_head100.log"

        # # 如果第一次出现的日志大于结束日期，跳过
        # if [[ "$first_date_in_this_file" > "$END_DATE" ]]; then
        #     echo "日志中第一次出现的日期为 $first_date_in_this_file，大于结束日期 $END_DATE，跳过"
        #     continue
        # else
        #     echo "日志中第一次出现的日期为 $first_date_in_this_file，小于等于结束日期 $END_DATE，继续处理文件"
        # fi

        # # 截取最后 100 行 WAL_DIR WAL 日志输出到 $FILE_NAME_tail100.log
        # $WAL_DUMP "$WAL_FILE" | tail -n 100 > "${FILE_NAME}_tail100.log"

        # echo "截取最后 100 行 WAL 日志输出到 ${FILE_NAME}_tail100.log"

        # # 匹配最后 100 行中最后一次出现的日期
        # last_date_in_this_file=$(tac "${FILE_NAME}_tail100.log" | grep -m 1 -E "COMMIT [0-9]{4}-[0-9]{2}-[0-9]{2}" | grep -o -E "[0-9]{4}-[0-9]{2}-[0-9]{2}")
        # echo "最后一次出现的日期：$last_date_in_this_file"
        # # 清除临时文件
        # rm "${FILE_NAME}_tail100.log"

        # # 如果最后一次出现的日志小于开始日期，跳过
        # if [[ "$last_date_in_this_file" < "$START_DATE" ]]; then
        #     echo "日志中最后一次出现的日期为 $last_date_in_this_file，小于开始日期 $START_DATE，跳过"
        #     continue
        # # 如果最后一次出现的日志大于结束日期，则处理完这个文件后, 后面的文件都不需要处理了
        # elif [[ "$last_date_in_this_file" > "$END_DATE" ]]; then
        #     echo "日志中最后一次出现的日期为 $last_date_in_this_file，大于结束日期 $END_DATE，后续文件不需要处理"
        #     break_flag = true
        # fi

        
        
        # 截取前 100 行 WAL 日志, 匹配第一次出现的日期
        # first_date_in_this_file=$(head -n 100 "$WAL_FILE" | grep -m 1 -E "COMMIT [0-9]{4}-[0-9]{2}-[0-9]{2}" | grep -o -E "[0-9]{4}-[0-9]{2}-[0-9]{2}")
        # echo "第一次出现的日期：$first_date_in_this_file"


        # # 使用 pg_waldump 处理每个WAL文件并分割结果
        # /usr/pgsql-15/bin/pg_waldump "$WAL_FILE" | split -l "$LINES_PER_PART" - "${WAL_FILE}_dump_"

        # # 处理每个分割后的文件
        # for PART_FILE in "${WAL_FILE}_dump_"*
        # do
        #     # 正序查找首次出现指定日期的行号
        #     first_line=$(grep -m 1 -E "COMMIT [0-9]{4}-[0-9]{2}-[0-9]{2}" "$PART_FILE")
        #     echo "首次出现指定日期的行号：$first_line"
        #     last_line=$(tac "$PART_FILE" | grep -m 1 -E "COMMIT [0-9]{4}-[0-9]{2}-[0-9]{2}")
        #     echo "最后一次出现指定日期的行号：$last_line"

        #     # 提取日期
        #     first_date=$(echo "$first_line" | grep -o -E "[0-9]{4}-[0-9]{2}-[0-9]{2}")
        #     echo "首次出现指定日期：$first_date"
        #     last_date=$(echo "$last_line" | grep -o -E "[0-9]{4}-[0-9]{2}-[0-9]{2}")
        #     echo "最后一次出现指定日期：$last_date"

        #     # 检查日期是否在范围内
        #     if [[ "$first_date" > "$END_DATE" ]] || [[ "$last_date" < "$START_DATE" ]]; then
        #         echo "Skipping part $PART_FILE as it does not contain relevant data."
        #         rm "$PART_FILE"
        #         continue
        #     fi

        #     # 根据首次和最后一次日期所在行号，截取日志内容
        #     first_line_num=$(echo "$first_line" | cut -f1 -d:)
        #     last_line_num=$(echo "$last_line" | cut -f1 -d:)
        #     echo "Processing part $PART_FILE from line $first_line_num to $last_line_num"

        #     # 截取和分割日志
        #     awk "NR>=$first_line_num && NR<=$last_line_num" "$PART_FILE" > "filtered_${PART_FILE##*/}.log"
        #     split -l "$LINES_PER_PART" "filtered_${PART_FILE##*/}.log" "${OUTPUT_BASE}_${PART_FILE##*/}-"

        #     # 清理临时文件
        #     rm "$PART_FILE" "filtered_${PART_FILE##*/}.log"
        # done
    else
        echo "当前文件不是WAL日志文件，跳过：$FILE_NAME"
    fi
    # 处理 break_flag
    if [[ "$break_flag" = true ]]; then
        break
    fi
done

echo "分割完成，输出文件为 ${OUTPUT_BASE}_*"
