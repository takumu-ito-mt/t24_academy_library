package jp.co.metateam.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.service.RentalManageService;
import jp.co.metateam.library.service.StockService;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import jp.co.metateam.library.model.RentalManage;

import jp.co.metateam.library.model.RentalManageDto;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.StockDto;
import jp.co.metateam.library.values.RentalStatus;
import jp.co.metateam.library.values.StockStatus;


/**
 * 貸出管理関連クラスß
 */
@Log4j2
@Controller
public class RentalManageController {

    private final AccountService accountService;
    private final RentalManageService rentalManageService;
    private final StockService stockService;

    @Autowired
    public RentalManageController(
        AccountService accountService, 
        RentalManageService rentalManageService, 
        StockService stockService
    ) {
        this.accountService = accountService;
        this.rentalManageService = rentalManageService;
        this.stockService = stockService;
    }

    /**
     * 貸出一覧画面初期表示
     * @param model
     * @return
     */
    @GetMapping("/rental/index")
    public String index(Model model) {
        // 貸出管理テーブルから全件取得 <>の中はテーブル名
        List <RentalManage> rentalManageList = this.rentalManageService.findAll();
        // 貸出一覧画面に渡すデータをmodelに追加
        //データはmodelに入れて画面に表示する。addAttribute（A,B）Aは画面に渡す箱の名前Bは箱に入れるデータの名前、ここでは上のrentalManageList
        //"rental/index"の＄｛〇｝〇に書かれているのと第１引数は同じ
        model.addAttribute("rentalManageList", rentalManageList);
        // 貸出一覧画面に遷移
        return "rental/index";
    }


     @GetMapping("/rental/add")
     public String add(Model model) {
        
          List<Account> accounts = this.accountService.findAll();
          List<Stock> stockList = this.stockService.findAll();
        

          model.addAttribute("accounts", accounts);
          model.addAttribute("stockList", stockList);
          model.addAttribute("rentalStatus", RentalStatus.values());
  
        if (!model.containsAttribute("rentalManageDto")) {
            model.addAttribute("rentalManageDto", new RentalManageDto());
         }
          return "rental/add";
     }
  
     @PostMapping("/rental/add")
        public String save(@Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra) {
          try {
          if (result.hasErrors()) {
                 throw new Exception("Validation error.");
             }
            // 登録処理
            this.rentalManageService.save(rentalManageDto);

           return "redirect:/rental/index";
          } catch (Exception e) {
             log.error(e.getMessage());

             ra.addFlashAttribute("rentalManageDto", rentalManageDto);
             ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);

              return "redirect:/rental/add";
         }
    } 
   
    @GetMapping("/rental/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        //public String edit(@PathVariable("id") String id, Model model) {
        
    //ここの５行で情報を取得してプルダウンにセットする。プルダウンを押すと取得した情報が出てくる。
        List<Account> accounts = this.accountService.findAll();
        List<Stock> stockList = this.stockService.findStockAvailableAll();
            //model.addAttribute(第一引数、第二引数); 第二引数のデータを第一引数の型、箱に入れて、ビュー（HTML）に渡す
            //<option th:each="account : ${accounts}" th:value="${account.employeeId}"
        model.addAttribute("accounts", accounts);
            //<option th:each="stock : ${stockList}" th:value="${stock.id}"
        model.addAttribute("stockList", stockList);
             // <option th:each="status : ${rentalStatus}" th:value="${status.value}"
        model.addAttribute("rentalStatus", RentalStatus.values());
        

        //ifのブロックは、編集を行いたい貸出管理番号に設定された情報（社員番号、貸出・返却予定日、在庫管理番号、貸出ステータス、）ダミーデータ？をセットする。
        if (!model.containsAttribute("rentalManageDto")) {
            //インスタンスの生成　→　クラス名　変数名　= new クラス名(); 
            RentalManageDto rentalManageDto = new RentalManageDto();
            RentalManage rentalManage = this.rentalManageService.findById(id);
            //RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));変更前
            
            rentalManageDto.setId(rentalManage.getId()); 
            rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
            rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());
            rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
            rentalManageDto.setStockId(rentalManage.getStock().getId());
            rentalManageDto.setStatus(rentalManage.getStatus());

                // <form id="rental_add_form" th:object="${rentalManageDto}" th:action="@{/rental/add}" method="post">
            model.addAttribute("rentalManageDto", rentalManageDto);
        } 
      return "rental/edit";
    }
    //変更ボタン押下時に、入力した内容を受け取る。
    //@PostMappingの引数はedit.htmlのaction{}と同じにする。
    //<form id="rental_edit_form" th:object="${rentalManageDto}" th:action="@{/rental/{id}/edit(id=*{id})}" method="post">
    @PostMapping("/rental/{id}/edit")

    public String update(@PathVariable("id") String id, @Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra) {
        try {

            //変更前の情報を取得
            RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
            //変更前のステータスを渡して、Dtoでバリデーションチェックを行う。rentalManage.getStatus()で変更前のステータス
            String validerror = rentalManageDto.validationChecks(rentalManage.getStatus()); 
                if(validerror != null){
                result.addError(new FieldError("rentalManageDto", "status", validerror));
            }

        

            //バリデーションエラーがあるかどうかを判別している。エラーあり：例外を投げる　エラーなし：登録処理に移る
            if (result.hasErrors()) {
                throw new Exception("Validation error.");
             }
            // 登録処理　
            rentalManageService.update( Long.valueOf(id),rentalManageDto); 
                return "redirect:/rental/index";

        } catch (Exception e) {
            log.error(e.getMessage());

            //"rentalManageDto"という名前で、rentalManageDtoというオブジェクトを保存しています。次のページでこの情報を使うことができる。
            //エラーが発生すると、画面は遷移しないでそのまま編集画面にいる。⇒入力した値が
            ra.addFlashAttribute("rentalManageDto", rentalManageDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);

            return "redirect:/rental/" + id + "/edit";
        }
    }
}


   