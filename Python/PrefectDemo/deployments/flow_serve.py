from flows.basic_flow import basic_data_flow

if __name__ == "__main__":
    # 创建并运行部署
    basic_data_flow.serve(
        name="基础流程-每分钟",
        interval=60,
        parameters={"rows": 200, "output_path": "scheduled_basic_output.csv"},
        tags=["demo", "basic"],
    )
