package contracts.remindercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'set reminders by targetId'
    description 'should return status 200'
    request {
        method PUT()
        url($(
                consumer(regex("/api/reminders/" + uuid().toString())),
                producer("/api/reminders/fc2c6859-dcdb-470d-9fc6-cf21a1bf98b0")
        ))
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body([
                [
                        "periodicity": $(
                                consumer(anyNonEmptyString()),
                                producer("ONCE")
                        ),
                        "date"       : [
                                "time" : $(
                                        consumer(anyNumber()),
                                        producer(100)
                                ),
                                "date" : $(
                                        consumer(anyNumber()),
                                        producer(1)
                                ),
                                "month": $(
                                        consumer(anyNumber()),
                                        producer(1)
                                ),
                                "year" : $(
                                        consumer(anyNumber()),
                                        producer(2030)
                                ),
                                "timezone" : $(
                                        consumer(anyNonBlankString()),
                                        producer("Europe/Berlin")
                                ),
                        ]
                ]
        ])
    }
    response {
        status 201
    }
}
