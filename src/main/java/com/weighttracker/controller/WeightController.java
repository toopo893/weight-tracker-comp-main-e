package com.weighttracker.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.weighttracker.entity.User;
import com.weighttracker.entity.WeightRecord;
import com.weighttracker.service.UserService;
import com.weighttracker.service.WeatherService;
import com.weighttracker.service.WeightService;

@Controller
@RequestMapping("/")  // ベースURLを明示的に指定
public class WeightController {
    
    @Autowired
    private WeightService weightService;
    
    @Autowired
    private WeatherService weatherService;
    
    @Autowired
    private UserService userService;
    
    // ホームページを表示
    @GetMapping
    public String home(Model model) {
        // 現在ログインしているユーザーのIDを取得
        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        // ユーザー情報を取得
        User currentUser = userService.getUserById(userId);
        model.addAttribute("user", currentUser);
        
        // ユーザーの体重記録を取得
        List<WeightRecord> records = weightService.getWeightRecordsByUserId(userId);
        model.addAttribute("weightRecords", records);
        
        // 平均体重を計算
        Double averageWeight = weightService.calculateAverageWeight(userId);
        model.addAttribute("averageWeight", averageWeight);
        
        // 最新の体重記録がある場合、ランクを評価
        if (!records.isEmpty()) {
            Double latestWeight = records.get(0).getWeight();
            String rank = weightService.evaluateWeight(latestWeight, averageWeight);
            model.addAttribute("latestWeight", latestWeight);
            model.addAttribute("rank", rank);
        }
        
        // 目標体重との差を計算（目標体重が設定されている場合）
        if (currentUser.getTargetWeight() != null && !records.isEmpty()) {
            Double latestWeight = records.get(0).getWeight();
            Double targetDifference = latestWeight - currentUser.getTargetWeight();
            model.addAttribute("targetDifference", targetDifference);
        }
        
        // 天気情報を取得して追加
        Map<String, Object> weatherData = weatherService.getDefaultCityWeather();
        model.addAttribute("weatherData", weatherData);
        
        // 天気情報に基づく健康アドバイスを追加
        String healthAdvice = weatherService.generateHealthAdvice(weatherData);
        model.addAttribute("healthAdvice", healthAdvice);
        
        return "index";
    }
    
    // グラフページを表示
    @GetMapping("/chart")
    public String showChart(Model model) {
        // 現在ログインしているユーザーのIDを取得
        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        // ユーザー情報を取得
        User currentUser = userService.getUserById(userId);
        model.addAttribute("user", currentUser);
        
        // ユーザーの体重記録を取得
        List<WeightRecord> records = weightService.getWeightRecordsByUserId(userId);
        model.addAttribute("weightRecords", records);
        
        // 平均体重を計算
        Double averageWeight = weightService.calculateAverageWeight(userId);
        model.addAttribute("averageWeight", averageWeight);
        
        return "chart";
    }
    
    // 履歴ページを表示
    @GetMapping("/history")
    public String showHistory(Model model) {
        // 現在ログインしているユーザーのIDを取得
        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        // ユーザー情報を取得
        User currentUser = userService.getUserById(userId);
        model.addAttribute("user", currentUser);
        
        // ユーザーの体重記録を取得
        List<WeightRecord> records = weightService.getWeightRecordsByUserId(userId);
        model.addAttribute("weightRecords", records);
        
        // 平均体重を計算
        Double averageWeight = weightService.calculateAverageWeight(userId);
        model.addAttribute("averageWeight", averageWeight);
        
        return "history";
    }
    
    // 設定ページを表示
    @GetMapping("/settings")
    public String showSettings(Model model) {
        // 現在ログインしているユーザーのIDを取得
        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        // ユーザー情報を取得
        User currentUser = userService.getUserById(userId);
        model.addAttribute("user", currentUser);
        
        return "settings";
    }
    
    // 目標体重の設定を処理
    @PostMapping("/settings/target-weight")
    public String updateTargetWeight(@RequestParam("targetWeight") Double targetWeight) {
        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        userService.updateTargetWeight(userId, targetWeight);
        return "redirect:/settings";
    }
    
    // 詳細ページを表示
    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable("id") Long id, Model model) {
        // 現在ログインしているユーザーのIDを取得
        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        
        // 体重記録の詳細を取得
        WeightRecord record = weightService.getWeightRecordById(id);
        
        // 記録が現在のユーザーのものか確認
        if (!record.getUserId().equals(userId)) {
            return "redirect:/";  // 他のユーザーの記録へのアクセスを拒否
        }
        
        model.addAttribute("weightRecord", record);
        
        // 平均体重を計算
        Double averageWeight = weightService.calculateAverageWeight(userId);
        model.addAttribute("averageWeight", averageWeight);
        
        // ランクを評価
        String rank = weightService.evaluateWeight(record.getWeight(), averageWeight);
        model.addAttribute("rank", rank);
        
        return "details";
    }
    
    @PostMapping("/add")
    @SuppressWarnings("CallToPrintStackTrace")
    public String addWeight(@RequestParam("weight") Double weight, 
                        @RequestParam("recordedDate") String recordedDateStr) {
        // デバッグ出力を追加
        System.out.println("体重が送信されました: " + weight + ", 日付: " + recordedDateStr);
        
        try {
            // 現在ログインしているユーザーのIDを取得
            Integer userId = userService.getCurrentUserId();
            if (userId == null) {
                return "redirect:/login";
            }
            
            // 文字列からLocalDate型への変換
            LocalDate recordedDate = LocalDate.parse(recordedDateStr);
            
            // 選択された日付を使用して体重記録を保存
            weightService.saveWeightRecord(userId, weight, recordedDate);
            return "redirect:/";
        } catch (Exception e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/?error=true";
        }
    }
    
    // 体重記録を削除（CRUD機能の一部）
    @GetMapping("/delete/{id}")
    public String deleteWeight(@PathVariable("id") Long id) {
        try {
            // 現在ログインしているユーザーのIDを取得
            Integer userId = userService.getCurrentUserId();
            if (userId == null) {
                return "redirect:/login";
            }
            
            // 記録が現在のユーザーのものか確認
            WeightRecord record = weightService.getWeightRecordById(id);
            if (!record.getUserId().equals(userId)) {
                return "redirect:/";  // 他のユーザーの記録の削除を拒否
            }
            
            weightService.deleteWeightRecord(id);
        } catch (Exception e) {
            System.err.println("削除エラー: " + e.getMessage());
        }
        return "redirect:/";
    }
}