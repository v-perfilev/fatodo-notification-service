package com.persoff68.fatodo.model.vm;

import com.persoff68.fatodo.web.rest.validator.MonthConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MonthConstraint
public class MonthVM {
    int year;
    int month;
    String timezone;
}
