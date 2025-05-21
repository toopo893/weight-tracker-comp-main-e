package com.weighttracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class JokeController {

    private final List<String> jokes = List.of(
        "最近の体重計、嘘つくんだよね。昨日より増えてるわけないし！",
        "体重が増えたのは、空気を吸いすぎたせいだと思うんだ",
        "ダイエットは明日からって100回は言ってる気がする…",
        "もう体重計じゃなくて、体重『警』だよ。乗るたびに警告してくる！",
        "でも筋肉は重いって言うし…これは筋肉ってことでOK？"
    );

    @GetMapping("/joke")
    public String getRandomJoke() {
        Random random = new Random();
        return jokes.get(random.nextInt(jokes.size()));
    }
}
