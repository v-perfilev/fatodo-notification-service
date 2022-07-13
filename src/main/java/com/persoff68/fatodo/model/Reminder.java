package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.model.converter.ArrayListConverter;
import com.persoff68.fatodo.model.converter.DataParamsConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ftd_reminder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"thread", "notifications"})
public class Reminder extends AbstractAuditingModel {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private ReminderThread thread;

    private Periodicity periodicity;

    @Convert(converter = DataParamsConverter.class)
    private DateParams date;

    @Convert(converter = ArrayListConverter.class)
    private List<Integer> weekDays;

    @Convert(converter = ArrayListConverter.class)
    private List<Integer> monthDays;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastNotificationDate;

    @OneToMany(mappedBy = "reminder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public boolean isSameReminder(Reminder reminder) {
        return objectsEqual(this.periodicity, reminder.getPeriodicity())
                && objectsEqual(this.date, reminder.getDate())
                && integerListsEqual(this.weekDays, reminder.getWeekDays())
                && integerListsEqual(this.monthDays, reminder.getMonthDays());
    }

    public static boolean listContains(List<Reminder> list, Reminder item) {
        return list.stream().anyMatch(l -> l.isSameReminder(item));
    }

    private static <T> boolean objectsEqual(T o1, T o2) {
        return o1.equals(o2);
    }

    private static boolean integerListsEqual(List<Integer> list1, List<Integer> list2) {
        List<Integer> sortedList1 = list1.stream().sorted().toList();
        List<Integer> sortedList2 = list2.stream().sorted().toList();
        return sortedList1.equals(sortedList2);
    }

}
