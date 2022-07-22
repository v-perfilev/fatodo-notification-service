package contracts.remindercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get reminders by targetId'
    description 'should return status 200 and list of ReminderDTOs'
    request {
        method GET()
        url($(
                consumer(regex("/api/reminder/" + uuid().toString())),
                producer("/api/reminder/fc2c6859-dcdb-470d-9fc6-cf21a1bf98b0")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
                [
                        "periodicity": "ONCE",
                        "date"       : [
                                "time" : anyNumber(),
                                "date" : anyNumber(),
                                "month": anyNumber(),
                                "year" : anyNumber()
                        ],
                        "weekDays"   : [],
                        "monthDays"  : []
                ]
        ])
    }
}
