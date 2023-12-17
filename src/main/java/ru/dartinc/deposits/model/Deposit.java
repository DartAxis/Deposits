package ru.dartinc.deposits.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Getter
@Setter
public class Deposit {
    private Integer name;
    private BigDecimal amount;
    private BigDecimal yearPercent;
    private LocalDate startDate;
    private Account account;

    public Deposit(Integer name, BigDecimal amount, BigDecimal yearPercent, LocalDate startDate, Account account) {
        this.name = name;
        this.amount = amount;
        this.yearPercent = yearPercent;
        this.startDate = startDate;
        this.account = account;
        System.out.printf("Открываем счет №%s от %s на сумму: %.2f\n", this.name, this.startDate, this.amount.doubleValue());
    }

    public void checkDate(LocalDate date) {
        var nowMonth = date.getMonth().getValue();
        var nowDay = date.getDayOfMonth();
        var startMonth = startDate.getMonth().getValue();
        var startDay = startDate.getDayOfMonth();
        if (startDay == nowDay && nowMonth - startMonth >= 0) {
            BigDecimal monthPercent = BigDecimal.valueOf(yearPercent.doubleValue() / 12);
            var adding = this.amount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(monthPercent);
            this.account.setAmount(this.account.getAmount().add(adding));
            System.out.print(date + ": Пополнение основного счета за счет процентов со вклада №" + this.name + " от " + startDate + " на сумму: ");
            System.out.printf("%.2f\n", adding);
        }
        if (startDay == nowDay && (nowMonth - startMonth) % 3 == 0 && !date.equals(this.startDate)) {
            System.out.println("Закрываем счет №" + this.name + " от " + this.startDate);
            this.startDate = date;
            System.out.printf("Открываем счет №%s от %s на сумму: %.2f\n", this.name, this.startDate, this.amount.doubleValue());
        }
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "name=" + name +
                ", amount=" + String.format("%.2f", amount) +
                ", yearPercent=" + yearPercent +
                ", startDate=" + startDate +
                '}';
    }
}
