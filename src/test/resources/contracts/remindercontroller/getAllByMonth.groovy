package contracts.remindercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get calendar reminders by month'
    description 'should return status 200'
    request {
        method POST()
        url("/api/reminder/calendar")
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body([
                "year"    : $(
                        consumer(anyNumber()),
                        producer(2090)
                ),
                "month"   : $(
                        consumer(anyNumber()),
                        producer(10)
                ),
                "timezone": $(
                        consumer(anyNonBlankString()),
                        producer("Europe/Berlin")
                ),
        ])
    }
    response {
        status 200
        body([
                [
                        "parentId"   : anyUuid(),
                        "targetId"   : anyUuid(),
                        "periodicity": anyNonBlankString(),
                        "type"       : anyNonBlankString(),
                        "date"       : anyNonBlankString()
                ]
        ])
    }
}
