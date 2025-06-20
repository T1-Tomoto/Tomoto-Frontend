package com.example.tomoto.structure.model

object ChallengeListFactory {

    fun getDailyChallenges(): List<Challenge> = listOf(
        Challenge("아침형 인간", "10시 전에 앱 실행하기"),
        Challenge("하루 한 번의 집중", "하루 1회 집중 타이머 사용"),
        Challenge("뽀모도로 입문자", "하루 뽀모도로 2회 달성"),
        Challenge("뽀모도로 실천가", "하루 뽀모도로 4회 달성"),
        Challenge("뽀모도로 숙련자", "하루 뽀모도로 6회 달성"),
        Challenge("뽀모도로 마스터", "하루 뽀모도로 8회 달성"),
        Challenge("달콤한 휴식", "30분 휴식 취하기")
    )

    fun getPermanentChallenges(): List<Challenge> = listOf(
        Challenge("연속 집중 2단계", "연속으로 2회 집중 타이머 실행"),
        Challenge("연속 집중 4단계", "연속으로 4회 집중 타이머 실행"),
        Challenge("연속 집중 6단계", "연속으로 6회 집중 타이머 실행"),
        Challenge("뽀모도로 10회 완주", "누적 뽀모도로 10회 달성"),
        Challenge("뽀모도로 20회 완주", "누적 뽀모도로 20회 달성"),
        Challenge("뽀모도로 30회 완주", "누적 뽀모도로 30회 달성"),
        Challenge("뽀모도로 40회 완주", "누적 뽀모도로 40회 달성"),
        Challenge("뽀모도로 50회 완주", "누적 뽀모도로 50회 달성"),
        Challenge("뽀모도로 100회 정복", "누적 뽀모도로 100회 달성"),
        Challenge("도전 과제 입문자", "누적 과제 5개 완료"),
        Challenge("도전 과제 정복자", "누적 과제 10개 완료")
    )
}
