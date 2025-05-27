# Code snippets are only available for the latest version. Current version is 1.x
from msgraph.graph_service_client import GraphServiceClient
from msgraph.generated.users.item.calendar_view.calendar_view_request_builder import (
    CalendarViewRequestBuilder,
)
from kiota_abstractions.base_request_configuration import RequestConfiguration
import asyncio


# To initialize your graph_client
# Replace this with your actual authentication code
def initialize_graph_client():
    # Add your authentication code here
    # This is a placeholder
    return GraphServiceClient()


async def get_calendar_events():
    graph_client = initialize_graph_client()

    query_params = (
        CalendarViewRequestBuilder.CalendarViewRequestBuilderGetQueryParameters(
            start_date_time="2025-05-27T06:49:20.540Z",
            end_date_time="2025-06-03T06:49:20.540Z",
        )
    )

    request_configuration = RequestConfiguration(
        query_parameters=query_params,
    )

    result = await graph_client.me.calendar_view.get(
        request_configuration=request_configuration
    )
    return result


# Run the async function
if __name__ == "__main__":
    result = asyncio.run(get_calendar_events())
    print(result)
