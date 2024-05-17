package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jp.co.metateam.library.values.RentalStatus;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 貸出管理DTO
 */
@Getter
@Setter

public class RentalManageDto {
    //@getterがあるから省略されるので、getterのコードを書かなく書かなくてもいい。
    //@Setterがあるから省略されるので、setterのコードを書かなく書かなくてもいい。
    private Long id;

    @NotEmpty(message="在庫管理番号は必須です")
    private String stockId;

    @NotEmpty(message="社員番号は必須です")
    private String employeeId;

    @NotNull(message="貸出ステータスは必須です")
    
    private Integer status;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="貸出予定日は必須です")
    private Date expectedRentalOn;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="返却予定日は必須です")
    private Date expectedReturnOn;

    private Timestamp rentaledAt;

    private Timestamp returnedAt;

    private Timestamp canceledAt;

    private Stock stock;

    private Account account;

    public String validationChecks(Integer prestatus) {
            Date currentDate = new Date();
        if(prestatus == RentalStatus.RENT_WAIT.getValue() && status ==  RentalStatus.CANCELED.getValue()){
            return "貸出ステータスは「貸出待ち」から「キャンセル」に変更できません";
        }else if((prestatus == RentalStatus.RENTAlING.getValue() && status ==  RentalStatus.RENT_WAIT.getValue())){
            return  "貸出ステータスは「貸出中」から「貸出待ち」に変更できません";
        }else if((prestatus == RentalStatus.RENTAlING.getValue() && status ==  RentalStatus.CANCELED.getValue())){
            return  "貸出ステータスは「貸出中」から「キャンセル」に変更できません";
        }else if((prestatus == RentalStatus.RETURNED.getValue() && status ==  RentalStatus.RENT_WAIT.getValue())){
            return  "貸出ステータスは「返却済み」から「貸出待ち」に変更できません";
        }else if((prestatus == RentalStatus.RETURNED.getValue() && status ==  RentalStatus.CANCELED.getValue())){
            return  "貸出ステータスは「返却済み」から「キャンセル」に変更できません";
        }else if((prestatus == RentalStatus.RETURNED.getValue() && status ==  RentalStatus.RENTAlING.getValue())){
            return  "貸出ステータスは「返却済み」から「貸出中」に変更できません";
        }else if((prestatus == RentalStatus.CANCELED.getValue() && status ==  RentalStatus.RENT_WAIT.getValue())){
            return  "貸出ステータスは「キャンセル」から「貸出待ち」に変更できません";
        }else if((prestatus == RentalStatus.CANCELED.getValue() && status ==  RentalStatus.RENTAlING.getValue())){
            return  "貸出ステータスは「キャンセル」から「貸出中」に変更できません";
        }else if((prestatus == RentalStatus.CANCELED.getValue() && status ==  RentalStatus.RETURNED.getValue())){
            return  "貸出ステータスは「キャンセル」から「返却済み」に変更できません";
        }else if(status == RentalStatus.RENTAlING.getValue() && expectedRentalOn.after(currentDate)){
            return "貸出ステータスが貸出中の時、未来の日付を貸出予定日として選択することはできません";
        }else if(status == RentalStatus.RETURNED.getValue() && expectedReturnOn.after(currentDate)){
            return "貸出ステータスが返却済みの時、未来の日付を返却予定日として選択することはできません";
        }
            return null;
    }
}





    
 
    

       


