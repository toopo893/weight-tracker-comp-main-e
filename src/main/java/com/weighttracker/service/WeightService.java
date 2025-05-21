package com.weighttracker.service;

import com.weighttracker.entity.WeightRecord;
import com.weighttracker.repository.WeightRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WeightService {
    
    private static final int RECENT_RECORDS_LIMIT = 60; // 直近60件のデータを使用
    
    @Autowired
    private WeightRecordRepository weightRecordRepository;
    
    // 新しい体重記録を保存
    public WeightRecord saveWeightRecord(Integer userId, Double weight) {
        WeightRecord record = new WeightRecord(userId, weight, LocalDate.now());
        return weightRecordRepository.save(record);
    }

    // ユーザーの体重記録を取得

    public List<WeightRecord> getWeightRecordsByUserId(Integer userId) {
    return weightRecordRepository.findByUserIdOrderByRecordedDateDescTimestampDesc(userId);
}
    
    // ユーザーの平均体重を計算（直近60件のデータから）
    public Double calculateAverageWeight(Integer userId) {
        try {
            // 直近の指定件数から平均を計算（ネイティブクエリ使用）
            Double recentAverage = weightRecordRepository.calculateRecentAverageWeight(userId, RECENT_RECORDS_LIMIT);
            
            // null だった場合は全データから計算（代替手段）
            if (recentAverage == null) {
                // PageRequestを使用して最新の60件を取得
                Pageable pageable = PageRequest.of(0, RECENT_RECORDS_LIMIT);
                List<WeightRecord> recentRecords = weightRecordRepository.findByUserIdOrderByRecordedDateDesc(userId, pageable);
                
                if (recentRecords.isEmpty()) {
                    return null;
                }
                
                // 平均を手動で計算
                double sum = 0;
                for (WeightRecord record : recentRecords) {
                    sum += record.getWeight();
                }
                return sum / recentRecords.size();
            }
            
            return recentAverage;
        } catch (Exception e) {
            // エラーが発生した場合はログ出力して従来の方法で計算
            System.err.println("直近データからの平均計算に失敗しました: " + e.getMessage());
            return weightRecordRepository.calculateAverageWeight(userId);
        }
    }
    
    // 体重に基づいてランクを評価
    public String evaluateWeight(Double currentWeight, Double averageWeight) {
        if (averageWeight == null) {
            return "データ不足";
        }
        
        // 平均との差の絶対値
        double difference = Math.abs(currentWeight - averageWeight);
        
        // ランク付け（仕様通り）
        if (difference <= 0.5) {
            return "A";  // 平均から±0.5kg以内
        } else if (difference <= 1.0) {
            return "B";  // 平均から±0.5〜1.0kg
        } else {
            return "C";  // 平均から±1.0kg以上
        }
    }
    
    // 体重記録を削除
    public void deleteWeightRecord(Long id) {
        weightRecordRepository.deleteById(id);
    }
    
    // ID に基づいて体重記録を取得
    public WeightRecord getWeightRecordById(Long id) {
        return weightRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定されたIDの記録が見つかりません: " + id));
    }
    
    // 指定された日付で体重記録を保存
    public WeightRecord saveWeightRecord(Integer userId, Double weight, LocalDate recordedDate) {
        WeightRecord record = new WeightRecord(userId, weight, recordedDate);
        return weightRecordRepository.save(record);
    }
}