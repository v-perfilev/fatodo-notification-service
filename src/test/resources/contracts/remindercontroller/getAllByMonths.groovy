package contracts.remindercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get calendar reminders by month'
    description 'should return status 200'
    request {
        method GET()
        url($(
                consumer(regex("/api/reminder/calendar\\?.*")),
                producer("/api/reminder/calendar?yearFrom=2090&monthFrom=10&yearTo=2090&monthTo=10&timezone=Europe/Berlin")
        ))
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
    }
    response {
        status 200
        body([
                "2090_10": [[
                                    "parentId"   : anyUuid(),
                                    "targetId"   : anyUuid(),
                                    "periodicity": anyNonBlankString(),
                                    "type"       : anyNonBlankString(),
                                    "date"       : anyNonBlankString()
                            ]]
        ])
    }
}
