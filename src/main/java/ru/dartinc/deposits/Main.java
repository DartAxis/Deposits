package ru.dartinc.deposits;

import lombok.extern.slf4j.Slf4j;
import ru.dartinc.deposits.model.Account;
import ru.dartinc.deposits.model.Deposit;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Slf4j
public class Main {
    public static final Double POPOLNENIE = 50000.0; // пополнение основного счета раз в полмесяца
    public static final Double PREMIYA = 0.0; // получение годовой премии
    public static final Double START = 1050000.0; // стартовая сумма на счёте

    public static final Double YEAR_PERCENT=15.00; // годовой банковский процент
    public static final Integer ACC=4; // кол-во открываемых счетов формирующих пассивный доход



    public static void main(String[] args) {
        clearReport();
        var mainAccount = new Account();
        mainAccount.setAmount(BigDecimal.valueOf(START));
        var startDate = LocalDate.of(2024, 3, 29);
        var endDate = LocalDate.of(2064, 12, 31);
        LocalDate nowDate = startDate;
        var name = 1;
        while (!nowDate.equals(endDate)) {

            if (nowDate.getDayOfMonth() == 5 || nowDate.getDayOfMonth() == 20) {
                mainAccount.setAmount(mainAccount.getAmount().add(BigDecimal.valueOf(POPOLNENIE)));
            }
            if (nowDate.getDayOfMonth() == 25 && nowDate.getMonth().getValue() == 12) {
                mainAccount.setAmount(mainAccount.getAmount().add(BigDecimal.valueOf(PREMIYA)));
            }
            if (nowDate.getDayOfMonth() % 7 == 0 && mainAccount.getAmount().doubleValue() >= 50000) {
                if (mainAccount.getDeposits().size() < ACC) {
                    while(mainAccount.getAmount().doubleValue()>0) {
                        BigDecimal adding;
                        if(mainAccount.getAmount().doubleValue()>1400000) {
                            adding = BigDecimal.valueOf(1400000);
                            mainAccount.getDeposits().add(new Deposit(name, adding
                                    , BigDecimal.valueOf(YEAR_PERCENT)
                                    , nowDate
                                    , mainAccount));
                            mainAccount.setAmount(mainAccount.getAmount().subtract(BigDecimal.valueOf(1400000)));
                        } else {
                            adding = mainAccount.getAmount();
                            mainAccount.getDeposits().add(new Deposit(name, adding
                                    , BigDecimal.valueOf(YEAR_PERCENT)
                                    , nowDate
                                    , mainAccount));
                            mainAccount.setAmount(BigDecimal.ZERO);
                        }
                        name++;
                    }
                } else {
                    resheto(mainAccount, nowDate);
                }
            }
            for (Deposit depo : mainAccount.getDeposits()) {
                if (!depo.getStartDate().equals(nowDate)) {
                    depo.checkDate(nowDate);
                }
            }
            nowDate = nowDate.plusDays(1L);
            if (nowDate.getDayOfMonth() == 1)
            {
                System.out.println();
            }
            if ((int) (mainAccount.getDeposits().stream().mapToDouble(x -> x.getAmount().doubleValue()).sum()) == ACC*1400000)
                break;
            if (nowDate.getYear() > startDate.getYear() && nowDate.getMonth().getValue() == 1 && nowDate.getDayOfMonth() == 1)
                yearStatic(mainAccount, nowDate, startDate);
        }
        System.out.println();
        System.out.println("Дата начала построения лесенки: " + startDate);
        System.out.println("Достигли предельного результата на дату: " + nowDate);
        System.out.printf("Начальная сумма на счету была: %.2f\n", START);
        System.out.printf("Сумма ежемесячного пополнения была 2 раза в месяц по %.2f\n", POPOLNENIE);
        System.out.printf("Размер счёта на окончании лесенки: %.2f\n", mainAccount.getAmount().doubleValue());
        System.out.println("Всего вкладов: " + mainAccount.getDeposits().size());
        for (Deposit depo : mainAccount.getDeposits()) {
            System.out.println(depo);
        }
        var sum = mainAccount.getDeposits().stream().mapToDouble(x -> x.getAmount().doubleValue()).sum();
        System.out.printf("Суммарно на депозитах: %.2f", sum);
    }

    private static void clearReport() {
        try {
            Path path = Paths.get("years_report.txt");
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void resheto(Account mainAccount, LocalDate nowDate) {
        var depos = mainAccount.getDeposits().stream().filter(x -> {
            var nowMonth = nowDate.getMonth().getValue();
            var nowDay = nowDate.getDayOfMonth();
            var startDay = x.getStartDate().getDayOfMonth();
            var startMonth = x.getStartDate().getMonth().getValue();
            return (nowMonth - startMonth) % 3 == 0 && nowDay == startDay;
        }).collect(Collectors.toList());
        try {
            var depo = depos.stream().min((x1, x2) -> Double.compare(x1.getAmount().doubleValue(), x2.getAmount().doubleValue())).get();
            if (depo.getAmount().doubleValue() + mainAccount.getAmount().doubleValue() < 1400000) {
                if (mainAccount.getAmount().doubleValue() > 50000) {
                    depo.setAmount(depo.getAmount().add(mainAccount.getAmount()));
                    System.out.println("Пополняем депозит №" + depo.getName() + " на сумму: " + mainAccount.getAmount().intValue());
                    mainAccount.setAmount(BigDecimal.ZERO);
                }
            } else {
                var minus = BigDecimal.valueOf(1400000).subtract(depo.getAmount());
                if (mainAccount.getAmount().doubleValue() > 50000 && minus.doubleValue() > 2) {

                    System.out.println("Пополняем депозит №" + depo.getName() + " на сумму: " + minus.intValue());
                    depo.setAmount(depo.getAmount().add(minus));
                    mainAccount.setAmount(mainAccount.getAmount().subtract(minus));
                }
            }
        } catch (Exception e) {

        }
    }

    public static void yearStatic(Account mainAccount, LocalDate nowDate, LocalDate startDate) {
        var result = new StringBuilder();
        result.append("\n")
                .append(String.format("Отчет на начало %s года\n", nowDate.getYear()))
                .append("Дата начала построения лесенки: ").append(startDate).append("\n")
                .append(String.format("Начальная сумма на счету была: %.2f\n", START))
                .append(String.format("Сумма ежемесячного пополнения была 2 раза в месяц по %.2f\n", POPOLNENIE))
                .append(String.format("Размер счёта на окончании лесенки: %.2f\n", mainAccount.getAmount().doubleValue()))
                .append(String.format("Всего вкладов: %d\n", mainAccount.getDeposits().size()));
        for (Deposit depo : mainAccount.getDeposits()) {
            result.append(depo.toString()).append("\n");
        }
        var sum = mainAccount.getDeposits().stream().mapToDouble(x -> x.getAmount().doubleValue()).sum();
        result.append(String.format("Суммарно на депозитах: %.2f\n", sum))
                .append(String.format("Общая сумма средств : %.2f\n\n", sum + mainAccount.getAmount().doubleValue()));
        System.out.println(result);

        try {
            Path path = Paths.get("years_report.txt");
            if (Files.exists(path)) {
                Files.writeString(path, result.toString(), StandardOpenOption.APPEND);
            } else {
                Files.writeString(path, result.toString(), StandardOpenOption.CREATE_NEW);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
