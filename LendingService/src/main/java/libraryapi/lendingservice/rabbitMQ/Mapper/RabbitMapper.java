package libraryapi.lendingservice.rabbitMQ.Mapper;

import libraryapi.lendingservice.model.Lending;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RabbitMapper {
    public static Lending StringToLending(String input) {
        Pattern pattern = Pattern.compile(
                "Lending\\{id=(null|\\d+),\\s*lendingCode='(.*?)',\\s*bookId=(\\d+),\\s*bookTitle='(.*?)',\\s*" +
                        "lendDate=(\\d{4}-\\d{2}-\\d{2}),\\s*limitDate=(\\d{4}-\\d{2}-\\d{2}),\\s*" +
                        "returnedDate=(null|\\d{4}-\\d{2}-\\d{2}),\\s*daysTillReturn=(\\d+),\\s*" +
                        "returned=(true|false),\\s*daysOverdue=(\\d+),\\s*fine=([\\d\\.]+),\\s*" +
                        "comment='(.*?)',\\s*readerId=(\\d+)\\}"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Long id = "null".equals(matcher.group(1)) ? null : Long.parseLong(matcher.group(1));
            String lendingCode = matcher.group(2);
            Long bookId = Long.parseLong(matcher.group(3));
            String bookTitle = matcher.group(4);
            LocalDate lendDate = LocalDate.parse(matcher.group(5));
            LocalDate limitDate = LocalDate.parse(matcher.group(6));
            LocalDate returnedDate = "null".equals(matcher.group(7)) ? null : LocalDate.parse(matcher.group(7));
            int daysTillReturn = Integer.parseInt(matcher.group(8));
            boolean returned = Boolean.parseBoolean(matcher.group(9));
            int daysOverdue = Integer.parseInt(matcher.group(10));
            float fine = Float.parseFloat(matcher.group(11));
            String comment = matcher.group(12);
            Long readerId = Long.parseLong(matcher.group(13));

            Lending lending = new Lending();
            lending.setId(id);
            lending.setLendingCode(lendingCode);
            lending.setBookId(bookId);
            lending.setBookTitle(bookTitle);
            lending.setLendDate(lendDate);
            lending.setLimitDate(limitDate);
            lending.setReturnedDate(returnedDate);
            lending.setDaysTillReturn(daysTillReturn);
            lending.setReturned(returned);
            lending.setDaysOverdue(daysOverdue);
            lending.setFine(fine);
            lending.setComment(comment);
            lending.setReaderId(readerId);

            return lending;
        }

        return null;
    }
}
