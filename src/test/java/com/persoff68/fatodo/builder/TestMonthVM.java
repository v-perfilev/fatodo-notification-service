package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.vm.MonthVM;
import lombok.Builder;

public class TestMonthVM extends MonthVM {

    @Builder
    TestMonthVM(int year, int month, String timezone) {
        super(year, month, timezone);
    }

    public static TestMonthVMBuilder defaultBuilder() {
        return TestMonthVM.builder()
                .year(2090)
                .month(0)
                .timezone("Europe/Berlin");
    }

    public MonthVM toParent() {
        MonthVM vm = new MonthVM();
        vm.setYear(getYear());
        vm.setMonth(getMonth());
        vm.setTimezone(getTimezone());
        return vm;
    }

}
