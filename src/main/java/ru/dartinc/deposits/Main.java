package ru.dartinc.deposits;

import ru.dartinc.deposits.model.Account;
import ru.dartinc.deposits.model.Deposit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class Main {
    public static final Double POPOLNENIE = 50000.0; //раз в полмесяца
    public static final Double PREMIYA = 500000.0;
    public static final Double START = 0.0;


    public static void main(String[] args) {
        var mainAccount = new Account();
        mainAccount.setAmount(BigDecimal.valueOf(START));
        var startDate = LocalDate.of(2024,1,1);
        var endDate = LocalDate.of(2047,12,31);
        LocalDate nowDate = startDate;
        var name=1;
        while(!nowDate.equals(endDate)){

            if(nowDate.getDayOfMonth()==5 || nowDate.getDayOfMonth()==20){
                mainAccount.setAmount(mainAccount.getAmount().add(BigDecimal.valueOf(POPOLNENIE)));
            }
            if(nowDate.getDayOfMonth()==25 && nowDate.getMonth().getValue()==12){
                mainAccount.setAmount(mainAccount.getAmount().add(BigDecimal.valueOf(PREMIYA)));
            }
            if(nowDate.getDayOfMonth()%7==0 && mainAccount.getAmount().doubleValue()>=50000){
                if(mainAccount.getDeposits().size()<12) {
                    mainAccount.getDeposits().add(new Deposit(name,mainAccount.getAmount()
                            , BigDecimal.valueOf(14.5)
                            , nowDate
                            , mainAccount));
                    mainAccount.setAmount(BigDecimal.ZERO);
                    name++;
                } else {
                    resheto(mainAccount, nowDate);
                }
            }
            for( Deposit depo:mainAccount.getDeposits()) {
                if(!depo.getStartDate().equals(nowDate)){
                    depo.checkDate(nowDate);
                }
            }
            nowDate=nowDate.plusDays(1l);
            if(nowDate.getDayOfMonth()==1) System.out.println();
            if((int)(mainAccount.getDeposits().stream().mapToDouble(x-> x.getAmount().doubleValue()).sum())==16800000) break;
        }
        System.out.println();
        System.out.println("Дата начала построения лесенки: "+startDate);
        System.out.println("Достигли предельного результата на дату: "+nowDate);
        System.out.printf("Начальная сумма на счету была: %.2f\n",START);
        System.out.printf("Сумма ежемесячного пополнения была 2 раза в месяц по %.2f\n",POPOLNENIE);
        System.out.printf("Размер счёта на окончании лесенки: %.2f\n",mainAccount.getAmount().doubleValue());
        System.out.println("Всего вкладов: "+ mainAccount.getDeposits().size());
        for (Deposit depo:mainAccount.getDeposits()){
            System.out.println(depo);
        }
        var sum = mainAccount.getDeposits().stream().mapToDouble(x-> x.getAmount().doubleValue()).sum();
        System.out.printf("Суммарно на депозитах: %.2f", sum);
    }

    private static void resheto(Account mainAccount, LocalDate nowDate) {
        var depos = mainAccount.getDeposits().stream().filter( x -> {
            var nowMonth = nowDate.getMonth().getValue();
            var nowDay = nowDate.getDayOfMonth();
            var startDay = x.getStartDate().getDayOfMonth();
            var startMonth = x.getStartDate().getMonth().getValue();
            return (nowMonth - startMonth) % 3 == 0 && nowDay == startDay;
        }).collect(Collectors.toList());
        try {
            var depo = depos.stream().min((x1, x2) -> Double.compare(x1.getAmount().doubleValue(), x2.getAmount().doubleValue())).get();
            if(depo.getAmount().doubleValue()+mainAccount.getAmount().doubleValue()<1400000) {
                if(mainAccount.getAmount().doubleValue()>50000) {
                    depo.setAmount(depo.getAmount().add(mainAccount.getAmount()));
                    System.out.println("Пополняем депозит №"+depo.getName()+ " на сумму: "+ mainAccount.getAmount().intValue());
                    mainAccount.setAmount(BigDecimal.ZERO);
                }
            } else {
                var minus = BigDecimal.valueOf(1400000).subtract(depo.getAmount());
                if(mainAccount.getAmount().doubleValue()>50000 && minus.doubleValue()>2) {

                    System.out.println("Пополняем депозит №"+depo.getName()+ " на сумму: "+ minus.intValue());
                    depo.setAmount(depo.getAmount().add(minus));
                    mainAccount.setAmount(mainAccount.getAmount().subtract(minus));
                }
            }
        } catch (Exception e){

        }
    }


}
