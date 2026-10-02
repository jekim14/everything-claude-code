# 쑥쑥 놀이터 근거 기반 설계를 위한 문헌 연구

> 작성: 2026년 9월 · 대상: 만 4~5세 유아용 누리과정 연계 놀이 학습 앱
> 이 문서는 앱의 설계 결정과 개선 로드맵이 어떤 연구 근거에 기대고 있는지 정리한 것입니다.

## 0. 연구 방법과 한계

- **범위.** 다섯 분야를 조사했습니다.
  1. 유아용 교육 앱과 디지털 미디어
  2. 유아 수학
  3. 한글과 초기 문해
  4. 사회정서학습(SEL)과 동기(칭찬·보상·피드백)
  5. 실행기능, 신체활동, 음악, 미술

  국외 문헌과 국내 문헌(KCI 등)을 함께 살폈습니다.
- **문헌 확인 방법.** 서지 정보는 PubMed 메타데이터(PMID·DOI)와 웹 검색 결과 페이지(제목·저자·연도)로 확인했습니다. 확인하지 못한 문헌은 본문 근거로 쓰지 않고 [부록 B](#부록-b-미확인-문헌)에 따로 모았습니다.
- **한계 1: 원문 미열람.** 조사 환경의 네트워크 정책 때문에 대부분의 학술지 원문 사이트에 들어가지 못했습니다(KCI, RISS, 육아정책연구소 저장소, 출판사 사이트 등). 그래서 **효과크기와 세부 결과는 초록이나 검색 요약에 근거합니다.** 수업이나 논문에 인용하시기 전에 원문을 꼭 대조해 주세요.
- **한계 2: 국내 문헌.** 국내 문헌은 검색 결과 요약으로만 확인했습니다. 그래서 국외 문헌보다 적게, 제한적으로 다뤘습니다. 특히 "한국 유아 대상 선형 수 보드게임 연구"와 "한국어판 글자-소리 학습 게임(GraphoGame류) 효과 연구"는 찾지 못했습니다. 이 두 주제는 국내 연구가 비어 있는 부분일 수 있습니다.
- **해석 원칙.** 메타분석, 무작위 대조 시험(RCT), 공식 지침을 우선했습니다. 상관연구와 소규모 준실험은 그렇다고 밝히고 인용했습니다.

---

## 1. 핵심 요약

1. **앱으로도 배울 수 있지만 효과는 중간 정도이고, 측정 방식에 따라 부풀려집니다.** 앱의 평균 효과는 +0.31 SD였고, 연구자가 만든 검사로 잴 때와 좁은 기초 기술(글자, 수 세기)을 잴 때 더 컸습니다(Kim et al., 2021). 앱 안의 정답률만으로 효과를 주장하지 말아야 합니다.
2. **효과적인 앱에는 공통된 요소가 있습니다.** 효과가 검증된 수학 앱들을 분석하니 세 가지가 함께 있었습니다(Outhwaite et al., 2023).
   - 왜 맞고 틀렸는지 알려 주는 **설명적 피드백**
   - 칭찬 같은 **동기적 피드백**
   - 아이 수준에 맞춰 **프로그램이 난이도를 조절하는 기능**

   그런데 시판 유아 앱에는 단계적으로 도와주는 피드백이 거의 없습니다(Callaghan & Reich, 2018).
3. **어른과 함께 쓰면 효과가 커지고, 앱 설계로 그 대화를 끌어낼 수 있습니다.** 성인과 함께 쓰면 학습 효과가 g≈0.2 더 컸습니다(Taylor et al., 2024). 앱 안에 **대화 거리(프롬프트)를 넣었을 때** 부모 언어의 질이 크게 좋아졌습니다(Mathers et al., 2025). 글자-소리 학습 게임도 어른이 함께할 때만 효과가 커졌습니다(McTigue et al., 2020).
4. **학습과 무관한 인터랙티브 요소는 방해가 됩니다.** 디지털 그림책에서 누르면 반응하는 요소(핫스팟), 미니게임, 사전 기능은 이해를 떨어뜨렸습니다. 이런 손해는 취약 가정 아동에게 더 컸습니다(Takacs et al., 2015; Bus et al., 2025).
5. **칭찬은 사람보다 과정을, 막연하기보다 구체적으로 해야 합니다.**
   - 사람 칭찬("넌 ○○를 잘하는 아이야")과 일반적 칭찬은 실패한 뒤의 끈기를 떨어뜨렸습니다(Kamins & Dweck, 1999; Cimpian et al., 2007; Zentall & Morris, 2010).
   - 반대로 걸음마기에 들은 과정 칭찬은 이후의 성장형 동기와 학업성취를 예측했습니다(Gunderson et al., 2013, 2018).
   - 다만 8세를 대상으로 한 사전등록 연구에서는 사람 칭찬과 과정 칭찬의 차이가 없었습니다(Bennett-Pierre et al., 2024). 효과는 맥락에 따라 달라질 수 있습니다.
6. **미리 약속된 보상은 내재동기를 떨어뜨릴 수 있습니다.** 활동을 하거나 끝냈다는 이유로 주는 예고된 보상은 자유선택 내재동기를 낮췄고(d≈−0.4), 긍정적 언어 피드백은 오히려 높였습니다(Deci et al., 1999). 반면 선택권을 주면 내재동기가 올라갔습니다(Patall et al., 2008). 다만 **앱 속 가상 스티커가 실물 보상과 같은 효과를 내는지는 직접 검증된 적이 없습니다.** 이 부분의 설계는 연구 결과에서 추론한 것입니다.
7. **한글 읽기의 첫 단위는 CV 음절 블록입니다.** 한국 유치원생의 경우, 낱자를 얼마나 아느냐보다 '가, 나'처럼 자음+모음 음절 블록을 알아보는 능력이 단어 읽기를 더 잘 예측했습니다(Cho, 2009). 자음 이름을 아는 것이 소리를 아는 것으로 저절로 이어지지는 않았습니다(Kim & Petscher, 2013). 그리고 누리과정은 읽기·쓰기를 "관심" 수준에서 다룹니다.
8. **수학에서는 기수 원리, 선형 수 보드게임, 패턴의 근거가 뚜렷합니다.** 세고 나서 마지막 수를 강조해 "모두 다섯 개"라고 말해 주는 방법이 효과적이었습니다(Paliwal & Baroody, 2018). 숫자가 적힌 **선형** 보드게임은 여러 차례 효과가 재현되었고(Ramani & Siegler, 2008; Siegler & Ramani, 2009), 메타분석 효과는 g=.21이었습니다(Nelson et al., 2025). 유아기 패턴 지식은 이후 수학 성취를 예측합니다(Rittle-Johnson et al., 2017; Zippert et al., 2020).
9. **실행기능 훈련은 효과가 작고, 다른 과제로 잘 옮겨가지 않습니다.** 반복 드릴보다 즐거운 활동 속에 실행기능 요구를 넣고, 난이도를 조금씩 올리는 방식이 권장됩니다(Diamond & Lee, 2011; Takacs & Kassai, 2019; Scionti et al., 2020).
10. **한국 유아는 많이 보고 적게 움직입니다.**
    - 만 3~4세의 하루 미디어 이용 시간은 평균 184.4분입니다(한국언론진흥재단, 2023).
    - 유아동의 스마트폰 과의존 위험군 비율은 약 26%입니다(과기정통부·NIA, 2025).
    - 0~6세 가운데 신체활동 지침을 지키는 아이는 18.2%입니다(Lee EY et al., 2024).
    - WHO는 3~4세에게 앉아서 보는 화면을 하루 1시간 이하, 신체활동을 하루 180분 이상 권고합니다(WHO, 2019).

---

## 2. 분야별 주요 발견

### 2.1 유아용 교육 앱과 디지털 미디어

**앱의 효과**
- 유치원~초3을 대상으로 한 앱 효과 메타분석(36편, 효과크기 285개)에서 평균 효과는 +0.31 SD였습니다(Kim et al., 2021).
- 6세 미만을 다룬 체계적 고찰은 특히 초기 수학에서 학습 효과를 확인했습니다. 다만 비뚤림 위험이 불명확한 연구가 많았습니다(Griffith et al., 2020).
- 영국 4~5세 389명을 대상으로 한 RCT에서, 구조화된 수학 앱을 12주 쓴 집단이 일반 수업 집단보다 수학 성취가 높았습니다(Outhwaite et al., 2019).
- 국내 메타분석(33편)은 효과크기 1.512를 보고했습니다(김남윤·김민정, 2024). 소규모 준실험이 많아 과대추정되었을 가능성을 감안해야 합니다. 제1저자 표기는 검색 결과마다 달라 원문 확인이 필요합니다.

**좋은 교육용 앱의 조건**
- Hirsh-Pasek et al.(2015)은 네 가지 조건과 명확한 학습목표를 제시했습니다.
  - 능동: 단순히 누르는 것이 아니라 생각하게 하는 활동
  - 몰입: 학습과 무관한 방해가 없는 상태
  - 의미: 아이의 삶과 연결된 내용
  - 사회적 상호작용: 사람과 주고받는 활동
- 인기 있는 '교육용' 앱은 대체로 이 기준 점수가 낮았습니다(Meyer et al., 2021).
- 탄자니아 XPRIZE 앱 비교에서 성과가 좋은 앱들은 명확한 과제 구조, 소근육 수준에 맞는 조작, 적정한 언어 부담, 개인화를 공통으로 갖추고 있었습니다(Huntington et al., 2023).

**방해 요소**
- 디지털 그림책에서 이야기와 맞는 애니메이션·음악은 도움이 됐지만, 핫스팟·게임·사전 기능은 방해가 됐습니다(Takacs et al., 2015; 43편).
- 2~8세 1,978명을 모은 메타분석에서도 미니게임은 이해를 떨어뜨렸습니다(Bus et al., 2025).
- 흥미를 끌지만 학습과 무관한 내용(유혹적 세부정보)은 학습에 해로웠습니다(g=−0.16; Sundararajan & Adesope, 2020).
- 장식이 많은 교실에서 유치원생은 과제에서 더 자주 벗어났습니다(Fisher et al., 2014).
- 효과음이 많은 전자책은 부모와 아이의 대화를 줄였습니다(Munzer et al., 2019).

**함께 쓰기(공동 미디어 참여)**
- 성인과 함께 쓰면 학습 효과가 g≈0.18~0.20 더 컸습니다(Taylor et al., 2024; 0~6세 17편).
- 스크린 시간이 길수록 언어능력은 낮았지만(r=−0.14), 교육용 콘텐츠(r=0.13)와 함께 보기(r=0.16)는 언어능력과 정적으로 관련됐습니다(Madigan et al., 2020).
- 앱에 내장된 대화 프롬프트가 부모의 언어 입력을 가장 크게 바꿨습니다(g=0.84~0.99; Mathers et al., 2025; 2~7세 627쌍).

**음성**
- 대화형 에이전트가 이야기를 읽어 주며 질문하자, 사람이 함께 읽을 때와 비슷하게 이야기 이해가 좋아졌습니다(Xu et al., 2022; 3~6세 117명 RCT).
- 억양이 풍부한 음성이 단조로운 음성보다 몰입과 지연 회상에 유리했습니다(Kory Westlund et al., 2017).

**조작적 설계와 사용 종료**
- 3~5세가 가장 오래 쓰는 앱들에는 조작적 설계가 흔했고, 저소득 가정일수록 더 많았습니다(Radesky et al., 2022).
  - 매력적인 미끼: 45.1%
  - 캐릭터를 이용한 압박("친구가 기다려요" 등): 24.8%
- '5세 이하'용 인기 앱의 95%에 광고가 있었습니다(Meyer et al., 2019).
- 부모가 끝내게 하는 것보다 앱이 종료를 안내할 때 사용 종료가 더 순조로웠습니다(Hiniker et al., 2016).

**지침과 한국 현황**
- WHO(2019)는 3~4세에게 앉아서 보는 화면을 하루 1시간 이하(적을수록 좋음)로 권고합니다. AAP(2016)는 2~5세에게 고품질 콘텐츠를 하루 1시간까지 보되 어른이 함께 보라고 권고합니다.
- 전 세계 2~5세 가운데 이 지침을 지키는 비율은 35.6%입니다(McArthur et al., 2022).
- 한국 현황
  - 유아동(3~9세) 스마트폰 과의존 위험군은 25.0%(2023) → 25.9%(2024) → 26.0%(2025)로 늘었습니다(과기정통부·한국지능정보사회진흥원).
  - 0~6세의 69.2%가 만 3세 이전에 스마트기기를 쓰기 시작했습니다(이정원 외, 2021).
  - 어머니의 이용 지도가 스마트미디어 이용이 발달에 미치는 영향을 조절했습니다(김은지·전귀연, 2020; 상관연구).

### 2.2 유아 수학

**초기 수학의 중요성**
- 입학 시점의 수학 능력이 이후 성취를 가장 강하게 예측했습니다(Duncan et al., 2007).
- 저소득층 아동을 추적한 연구에서 유아기의 수 세기와 **패턴** 지식이 초등 5학년 수학을 예측했습니다(Rittle-Johnson et al., 2017).

**기수 원리: 센 뒤 마지막 수가 전체 개수라는 이해**
- 2~5세 RCT에서 "세기 → 마지막 수 강조 → 총수 말하기"(ES≈1.5)가 세기만 하는 것보다 효과가 컸습니다(Paliwal & Baroody, 2018).
- 부모가 **눈앞에 있는 4~10개 묶음을 세거나 수를 붙여 말하는 것**이 이후 기수 지식을 예측했습니다(Gunderson & Levine, 2011).

**구조화된 수량**
- 주사위나 손가락처럼 구조화된 배열의 수량을 파악하는 능력이 1학년 덧셈을 예측했습니다(Kreilinger et al., 2021).
- 점 주사위로 보드게임을 한 RCT에서 수 세기와 구조 인식이 향상되었고, 이 효과는 1년 뒤까지 유지되었습니다(Gasteiger et al., 2021).

**선형 수 보드게임**
- 1~10이 적힌 선형 보드를 15분씩 4회 했더니 저소득층 유아의 수 크기 비교, 수직선 추정, 수 세기, 숫자 읽기가 향상되었고 9주 뒤에도 유지되었습니다. **칸에 숫자 없이 색만 있는 보드는 효과가 없었습니다**(Ramani & Siegler, 2008; Siegler & Ramani, 2008).
- 원형보다 선형 보드가 효과적이었습니다(Siegler & Ramani, 2009).
- 말을 옮길 때 1부터 다시 세지 않고 **현재 칸 다음 수부터 이어 세면** 학습 효과가 컸습니다(Laski & Siegler, 2014).
- 메타분석 효과는 g=.21이었고, 함께 노는 사람과 투입량이 효과를 조절했습니다(Nelson et al., 2025).

**근사 수 감각(ANS) 훈련**
- 근사 수 감각 훈련은 결과가 엇갈립니다(Park et al., 2016; Szkudlarek et al., 2021 재현 실패).
- 수학 성취와의 관련은 점 비교 같은 비상징 비교(r=.24)보다 **숫자 비교**(r=.30)와 수직선 추정(r=.44)이 더 컸습니다(Schneider et al., 2017, 2018).

**교육과정과 앱**
- 학습 경로(learning trajectory) 기반 교육과정인 Building Blocks는 큰 효과를 보였습니다(Clements & Sarama, 2008; Clements et al., 2011, g=0.72).
- 유아 수학 프로그램 메타분석의 효과는 d=0.62였습니다(Wang et al., 2016).
- 국내에서는 만 5세 42명이 앱 수학활동을 20회 한 뒤 수학적 성향과 문제해결력이 향상되었다는 준실험이 있습니다(김유나 외, 2025).

**패턴**
- 반복 패턴 지식은 작업기억과 이전 수학 점수를 통제한 뒤에도 유치원 수 지식을 예측했습니다(Zippert et al., 2020; Wijns et al., 2021).
- 같은 규칙을 다른 재료로 다시 만드는 **패턴 추상화**는 "A-B-A-B" 같은 추상 명칭을 쓸 때 더 잘 되었습니다(Fyfe et al., 2015).
- 다만 5회 패턴 튜터링은 패턴 지식만 올렸고 수 지식은 올리지 못했습니다(Zippert et al., 2021). 따라서 "패턴 놀이로 수학 실력이 오른다"고 과장해서는 안 됩니다.

**공간과 도형**
- 공간 훈련은 효과가 크고 오래 지속되었습니다(g=0.47; Uttal et al., 2013). 수학으로 옮겨가는 효과는 g=.28로 작았고, 구체물 조작이 컴퓨터 훈련보다 효과적이었습니다(Hawes et al., 2022).
- 유아는 '전형적인' 모양에 기대어 도형을 판단합니다(Clements et al., 1999; Satlow & Newcombe, 1998). 도형 속성은 **안내된 놀이**로 배울 때 가장 잘 익혔습니다(Fisher et al., 2013).
- 국내 연구에서도 기하 능력은 3·4·5세 사이에 모두 차이가 났습니다(홍혜경·김세루, 2008).

**가정 수학**
- 양육자가 참여하는 비형식 수학 중재의 효과는 g=0.26이었고, 부모 교육과 후속 지원이 있으면 약 .42였습니다(Nelson et al., 2024).
- 국내에서는 부모의 수학적 상호작용이 부모의 기대와 유아 수학 능력 사이를 매개했습니다(임혜성 외, 2019).

### 2.3 한글과 초기 문해

**음절 단위와 음운인식**
- 한국 유치원생은 낱자보다 CV 음절을 더 잘 알아봤습니다. 음절 지식은 이후 낱자 지식과 초성·종성 인식을 이끌었습니다(Cho, 2009; 6개월 종단연구).
- 한글은 알파벳 원리를 음절 단위로 표기하는 '알파음절문자'입니다(Pae, 2011).
- 다만 어릴수록 블록 안의 낱자 수 효과가 컸습니다(Ju et al., 2022). 음절과 낱자를 함께 다뤄야 한다는 뜻입니다.
- 음운인식은 음절 → 음절체 → 각운 → 음소 순으로 발달합니다. 과제는 합성 → 변별 → 탈락 → 대치 순으로 어렵고, 대치는 6세에 급격히 발달합니다(이숙·김화수, 2014; 김애화, 2012).
- 한국어에서는 영어식 초성-각운 경계(c-at)보다 **음절체-종성 경계**(ca-t)가 더 두드러집니다(Kim YS, 2007).

**자음 이름과 소리**
- 자음 이름을 아는 것은 읽기를 강하게 예측합니다(Kim YS, 2009; Kim & Petscher, 2011).
- 유아는 이름을 소리(자소-음소 대응)보다 먼저 익혔습니다(안성우·허민정, 2011).
- 이름의 구조가 낱자 난이도를 좌우했습니다. 특히 **기역·디귿·시옷**은 이름이 불규칙합니다(Kim & Petscher, 2013).
- 자음 소리 지식과 한 획을 더해 소리가 세지는 가획 원리 지식은 불연속적으로 발달했습니다(최나야·이순형, 2007).
- 받침과, 소리와 표기가 다른 음절은 더 늦게 익힙니다(Cho & McBride, 2022).

**국제 메타분석**
- 음소인식 교수의 효과는 d=0.53이었고, **글자와 함께 가르칠 때** 더 컸습니다(Ehri et al., 2001).
- 알파벳 교수가 음운·읽기로 옮겨가는 효과는 작았습니다(Piasta & Wagner, 2010). 그래서 소리 대응과 합성은 따로 명시적으로 다뤄야 합니다.

**디지털 문해**
- 컴퓨터를 활용한 유아 문해 개입의 효과는 0.28이었습니다(Verhoeven et al., 2020).
- 글자-소리 학습 게임 GraphoGame 연구 28편에서 단어 읽기 효과는 사실상 0이었고, 어른과 상호작용할 때만 효과가 커졌습니다(McTigue et al., 2020).

**이야기 읽기**
- 대화식 읽기의 효과는 4~5세에서 크게 줄었습니다(Mol et al., 2008).
- 공유 읽기는 활동을 하는 통제군과 비교하면 효과가 거의 없었습니다(Noble et al., 2019).

**쓰기**
- 손으로 쓰는 연습이 타이핑보다 글자 알아보기에 유리했습니다(Longcamp et al., 2005).
- 5세 fMRI 실험에서 **자유롭게 손으로 쓴 뒤에만** 읽기 관련 뇌 회로가 활성화됐고, 따라 그리기 뒤에는 그렇지 않았습니다(James & Engelhardt, 2012).
- 손가락으로 글자를 더듬는 시각·촉각 탐색은 알파벳 원리 이해를 높였습니다(Bara et al., 2004).
- 보고 가린 뒤 쓰기(지연 복사)는 한글 단어 읽기·철자를 설명했습니다(Cho, 2021).
- 획순 지도의 효과를 직접 검증한 연구는 찾지 못했습니다.

**정책 맥락**
- 2019 개정 누리과정 의사소통 영역에서 읽기·쓰기는 '관심 가지기' 수준으로 제시됩니다. 이 내용은 해설서 문서가 있다는 것만 확인했고, 세부 문구는 검색 요약으로 확인했습니다.
- 교육부는 2017년부터 초등 1~2학년 한글 교육을 68시간으로 늘리고 1학년 1학기 받아쓰기를 지양하도록 했습니다. **체계적인 한글 교육은 학교가 맡는다**는 전제입니다.

### 2.4 사회정서와 정서 지식

**정서 지식의 중요성**
- 정서 지식은 사회적 유능성과 행동 문제에 작거나 중간 정도로 관련됩니다(Trentacosta & Fine, 2010; 메타분석).
- 5세의 표정 인식·명명 능력은 9세의 사회적 행동과 학업 유능성을 예측했습니다(Izard et al., 2001).
- 3~4세의 정서 유능성은 유치원 시기의 사회적 유능성으로 이어졌습니다(Denham et al., 2003).
- 갈등 상황에서 아이가 떠올린 감정과 행동 선택을 함께 보면 학교 적응을 예측할 수 있었습니다(Denham et al., 2013).

**정서 어휘의 발달 순서**
- **기쁨 → 분노 → 슬픔 → 무서움 → 놀람 → 혐오** 순으로 나타납니다(Widen & Russell, 2003).
- 복합정서는 3~5세도 다른 사람의 이야기에서는 절반가량 알아보지만, 자기 경험으로 말하는 아이는 드물었습니다(Smith et al., 2015). 짧은 훈련은 4~5세에게 효과가 적었습니다(Peng et al., 1992).

**SEL 프로그램**
- 유아 SEL 메타분석(2~6세 63편)의 효과는 정서 유능성 d=0.54, 사회적 유능성 0.30, 행동 자기조절 0.28이었습니다(Blewitt et al., 2018).
- 여러 RCT에서 효과가 확인되었습니다: Preschool PATHS(Domitrovich et al., 2007), Head Start REDI(Bierman et al., 2008), Incredible Years(Webster-Stratton et al., 2008).
- PATHS 메타분석에서는 **실시량**이 가장 중요한 조절변인이었습니다(Shi et al., 2022).
- 국내 SEL 메타분석(20편)의 효과크기는 0.79였고, 16회기 이상일 때 효과가 컸습니다(박현영·채수은, 2022).

**정서조절**
- 4~5세에 맞는 방법으로 **거북이 기법**(멈추기 → 숨 고르기 → 말로 하기; Schneider, 1974)과 주의 돌리기(Ratcliff et al., 2021)가 있습니다.
- 부모 감정코칭 프로그램 Tuning in to Kids는 아이의 정서 지식을 높이고 문제행동을 줄였습니다(Havighurst et al., 2010). 국내 감정코칭 부모교육 메타분석에서도 중간 이상의 효과가 나타났습니다(조하영·윤미승, 2022).

**디지털 SEL**
- 디지털 SEL의 효과는 출판편향을 보정하면 g=0.37이었습니다(Cai et al., 2026).
- Daniel Tiger 앱을 사용한 아이들은 앱에서 가르친 정서조절 전략을 더 자주 썼습니다(Rasmussen et al., 2019). 다만 부모의 적극적 중재가 이 효과를 어느 방향으로 조절하는지는 원문으로 확인하지 못했습니다.

### 2.5 동기: 칭찬, 보상, 피드백, 캐릭터

**칭찬**
- 사람 칭찬을 받은 5~6세는 좌절한 뒤 무력하게 반응했습니다(Kamins & Dweck, 1999).
- "그림 잘 그리는 아이" 같은 일반적 칭찬은 조금만 섞여도 끈기를 떨어뜨렸습니다(Cimpian et al., 2007; Zentall & Morris, 2010).
- 걸음마기의 과정 칭찬 비율이 7~8세의 성장형 동기와 4학년 성취를 예측했습니다(Gunderson et al., 2013, 2018).
- 과장된 칭찬은 자존감이 낮은 아이의 도전을 줄였습니다(Brummelman et al., 2014).
- 좋은 칭찬의 조건은 진정성, 노력·전략 같은 통제 가능한 원인으로 돌리기, 자율 지지, 비교하지 않기입니다(Henderlong & Lepper, 2002).

**보상**
- 보상을 미리 약속받은 유아만 이후 그 활동에 대한 자발적 흥미가 줄었습니다(Lepper et al., 1973).
- 활동에 참여했다는 이유로 주는 보상(d=−0.40)과 끝냈다는 이유로 주는 보상(d=−0.36)은 내재동기를 떨어뜨렸습니다. 이 효과는 아동에게 더 컸습니다(Deci et al., 1999).
- 선택권은 내재동기를 높였고, 그 효과는 아동에게서 더 컸습니다(Patall et al., 2008).

**피드백**
- 오류 뒤에 교정 피드백을 주는 학습이 효과적입니다(Metcalfe, 2017).
- 효과적인 앱에는 설명적 피드백이 있었습니다(Outhwaite et al., 2023).

**캐릭터**
- 캐릭터와의 유대감(준사회적 관계)과 캐릭터가 아이 행동에 맞춰 반응하는 것이 수학 학습을 예측했습니다(Calvert et al., 2020).
- 반대로 캐릭터를 이용해 "더 놀아 줘"라고 압박하는 설계는 흔한 다크패턴입니다(Radesky et al., 2022).

### 2.6 실행기능

**훈련 효과**
- 3~6세 인지훈련의 실행기능 효과는 g=0.35였고, 행동·학습으로 옮겨가는 효과는 없었습니다(Scionti et al., 2020).
- 정상발달 유아만 보면 억제 g=0.10으로 더 작았습니다(Trujillo-Trujillo et al., 2026).
- 90편을 묶은 메타분석은 과제를 반복하는 명시적 훈련보다 **즐거운 일상 활동 속에 실행기능을 녹이는 방식**을 권했습니다(Takacs & Kassai, 2019).

**효과를 낸 프로그램의 원리**
- 반복 연습, 점진적 난이도, 정서·사회·신체의 통합입니다(Diamond & Lee, 2011).
- 놀이 중심 교육과정 Tools of the Mind는 여러 RCT에서 효과를 보였습니다(Diamond et al., 2007, 2019; Blair & Raver, 2014).

**연령 기준**
- 해 그림에 "밤", 달 그림에 "낮"이라고 반대로 말하는 과제(낮-밤 스트룹)는 5세 미만에게 매우 어렵고, 16번의 시행을 버티기 힘듭니다(Gerstadt et al., 1994).
- 색으로 분류하다가 모양으로 바꿔 분류하는 과제(DCCS)에서 3세는 이전 규칙을 고집하고, 5세 대부분은 규칙 전환에 성공합니다(Zelazo, 2006).
- '머리-발끝-무릎-어깨 반대로 하기' 과제(HTKS)는 유치원 학업성취를 예측합니다(Ponitz et al., 2009).

### 2.7 신체활동, 음악, 미술, 눈 건강

**신체활동**
- WHO(2019)는 3~4세에게 하루 180분 이상, 그중 60분 이상은 중·고강도로 움직이고 낮잠을 포함해 10~13시간 자도록 권고합니다.
- 한국 0~6세의 지침 충족률은 신체활동 18.2%, 좌식행동 38.1%였습니다(Lee EY et al., 2024).
- 꾸준한 신체활동은 실행기능을 높였고, 특히 게임형·인지형 활동의 효과가 컸습니다(Li et al., 2020; Liu et al., 2026). 반면 한 번 운동한 직후의 인지 효과는 거의 없었습니다(Song et al., 2023).
- 과제 내용과 연결된 동작을 하며 배운 4~5세는 수 개념 학습이 더 좋았습니다(Mavilidi et al., 2017).
- 계획된 기본 운동기술 프로그램은 효과가 있었지만(d=0.39), 자유놀이만으로는 향상이 없었습니다(Logan et al., 2012).

**음악**
- 매일 10분씩 20주 음악 활동을 하자 음운인식이 향상되었습니다(Degé & Schwarzer, 2011). 메타분석 효과는 d=0.2로 작았습니다(Gordon et al., 2015).
- 리듬·동작 프로그램은 억제 능력을 높였고, 그 효과가 6개월 뒤까지 유지되었습니다(Bentley et al., 2023).
- 그러나 설계 품질을 통제하면 음악 훈련이 다른 능력으로 옮겨가는 효과는 약 0이었습니다(Sala & Gobet, 2017, 2020). 함께 음악을 해서 친사회성이 늘었다는 연구(Kirschner & Tomasello, 2010)는 재현에 실패했습니다(Baier et al., 2021).

**미술과 소근육**
- 3~4세의 도형 따라 그리기 같은 소근육 능력은 유치원 성취를 예측했습니다(Cameron et al., 2012).
- 태블릿에 손가락으로 그리면 어린 아이도 알아볼 수 있는 그림을 더 많이 그렸습니다(Kirkorian et al., 2020).
- 그러나 소근육 정밀성은 태블릿보다 손으로 하는 놀이가 더 많이 향상시켰습니다(Lin et al., 2017).

**눈 건강**
- 스마트기기 사용은 근시와 약하게 관련되었지만 연구 질이 낮았습니다(Foreman et al., 2021).
- 야외활동을 늘리면 근시 발생이 줄었습니다(He et al., 2015 RCT; Kido et al., 2024 코크란 리뷰).
- 20분마다 먼 곳을 20초 보는 '20-20-20 규칙'은 성인에서 자각 증상만 줄였고, 아동 대상 근거는 없습니다(Talens-Estarelles et al., 2023).

---

## 3. 종합: 쑥쑥 놀이터 설계 원칙

| # | 원칙 | 핵심 근거 |
|---|---|---|
| 1 | **학습목표가 분명한 능동 과제**: 누르면 반응하는 요소를 늘리기보다 생각하게 만드는 조작을 넣는다 | Hirsh-Pasek 2015; Kirkorian 2016 |
| 2 | **설명적 피드백과 단계적 도움**: 오답에는 이유를 알려 주고, 연속 오답에는 힌트를 준다 | Outhwaite 2023; Metcalfe 2017; Callaghan & Reich 2018 |
| 3 | **아이 수준에 맞춘 난이도**: 나이 설정은 출발점으로만 쓰고 수행에 따라 조정한다 | Outhwaite 2023; Raudenbush 2020; Diamond & Lee 2011 |
| 4 | **과정·구체 칭찬**: 사람 칭찬과 과장 칭찬은 쓰지 않는다 | Kamins & Dweck 1999; Gunderson 2013; Brummelman 2014 |
| 5 | **예고된 보상 최소화와 선택권**: 매번 약속된 스티커 대신 깜짝 보상을 주고 아이가 고르게 한다 | Lepper 1973; Deci 1999; Patall 2008 |
| 6 | **어른과 함께하도록 설계**: 활동이 끝날 때마다 보호자 대화 거리를 제시한다 | Mathers 2025; Taylor 2024; McTigue 2020 |
| 7 | **방해 요소 배제**: 학습과 무관한 핫스팟·미니게임·장식을 넣지 않는다 | Takacs 2015; Bus 2025; Sundararajan 2020 |
| 8 | **발달 순서 존중**: 음절 → 낱자, 쉬운 정서부터, 연령별 실행기능 난이도 | Cho 2009; Widen & Russell 2003; Gerstadt 1994 |
| 9 | **화면 밖으로 이어 주기**: 하루 사용 한도를 두고, 움직임·야외·종이 놀이를 제안한다 | WHO 2019; Lee EY 2024; He 2015; Lin 2017 |
| 10 | **다크패턴 금지**: 광고, 연속 기록 압박, 캐릭터의 죄책감 유발을 하지 않는다 | Radesky 2022; Meyer 2019 |
| 11 | **효과를 과장하지 않기**: 보호자 안내에 "지능 향상", "수학 실력 향상" 같은 표현을 쓰지 않는다 | Sala & Gobet 2020; Zippert 2021; Kim 2021 |

---

## 4. 앱 개선 계획

### 4.1 이번 버전(2.0)에 반영한 것

| 영역 | 개선 | 근거 |
|---|---|---|
| 공통 | 칭찬 문구를 **과정 칭찬**으로 교체. 첫 시도에 맞히면 "딩동댕! 잘 보고 골랐구나.", 틀린 뒤 맞히면 "다시 생각해서 찾아냈구나." 판을 마치면 "차근차근 끝까지 해냈구나!" / "다시 생각하면서 끝까지 해냈구나!" | 2.5 |
| 공통 | 매번 주던 스티커와 별을 없애고, 문제가 있는 놀이를 **그날 처음 마쳤을 때만** 선물 상자 3개 중 하나를 **스스로 고르는** 깜짝 선물로 변경. 무작위 보상(도박형)은 쓰지 않음. 자유 놀이(그림·실로폰·체조)는 선물 없이 과정 칭찬만. 스티커 수·별 수 표시 제거 | 2.5, 2.1 |
| 공통 | 놀이를 마칠 때마다 보호자에게 **"함께 이야기해요" 대화 카드**(놀이별 2~3개). 보호자 화면 '함께 놀기'에 전체 목록과 화면 밖 놀이 | 2.1, 2.4, 2.2 |
| 공통 | **적응형 난이도(1~3단계)**: 나이는 출발점(만 4세 1단계, 만 5세 2단계)이고, 놀이마다 첫 시도 정답률 80% 이상이면 한 단계 올리고 50% 미만이면 내림 | 2.1, 2.6 |
| 공통 | **하루 놀이 시간**(기본 60분, 보호자가 30/45/90분·제한 없음 선택)과 20분마다 쉬어 가기. 끝나기 3분 전 예고, 홈 화면의 **해님 막대**로 남은 시간을 그림으로 표시. 쉬는 화면에서 **화면 밖 놀이 3가지** 제안(동그라미 찾기, 베개 징검다리, 그림책 읽기 등). 오늘 시간을 다 쓰면 보호자 확인 후 15분만 더 | 2.1, 2.7 |
| 한글 | 자음 **이름과 소리를 함께** 안내("기역! 기역은 '그' 소리. 고양이의 '고'!"). 첫 음절이 받침 없는 CV가 되도록 그림 낱말 교체(돼지→도넛, 물고기→무지개, 책→치즈, 원숭이→오리). ㅇ은 첫소리에서 소리가 없다고 안내 | 2.3 |
| 한글 | 새 놀이 **글자 만들기**: 자음·모음 조각을 넣으면 소리를 이어 읽음("느, 아, 나!"). 틀려도 만들어진 글자를 읽어 주고 필요한 소리를 알려 줌. 1단계는 ㅏ만, 2단계부터 ㅏ·ㅗ·ㅜ·ㅣ | 2.3 |
| 한글 | 글자 찾기에서 두 번 틀리면 정답 카드를 노란 테두리로 **힌트** | 2.1, 2.5 |
| 수학 | 숫자 세기: 맞히면 **"하나, 둘, 셋. 모두 세 개!"**로 기수를 강조. 첫 오답은 다시 세기 권유, 두 번째 오답은 **같이 세어 보여 주는 시범**과 정답 힌트 | 2.2, 2.1 |
| 수학 | 새 놀이 **숫자 징검다리**: 1~10이 적힌 일직선 돌을 주사위만큼 한 칸씩 건너며 **내려앉은 칸의 수를 말함**(이어 세기). 2단계부터 "몇에 도착했을까?" | 2.2 |
| 정서 | 감정 도입 순서를 발달에 맞게 조정(1단계: 기쁨·슬픔·화남·무서움, 2단계부터 +놀람). 이전 버전은 만 4세에서 무서움을 빼고 놀람을 넣어 순서가 반대였음 | 2.4 |
| 정서 | 그럴 법한 다른 감정(예: 천둥 → 놀람)은 오답으로 처리하지 않고 "그럴 수도 있어!"로 인정한 뒤 흔한 감정을 알려 줌. 화남·무서움 상황 뒤에는 원하면 **거북이 숨쉬기**(4초 들숨·4초 날숨 3번) | 2.4 |
| 실행기능 | 새 놀이 **멈춰! 놀이**(Go/No-Go): 초록 친구는 톡, 빨간 친구는 멈춤(누르는 친구 약 70%). 2단계부터 중간에 **"이번엔 반대로!"** 규칙 전환. 색과 모양(동그라미 체크/팔각형)을 함께 달리함 | 2.6 |
| 설계 | 배경을 평평한 색으로 바꾸고 해·꽃 등 계속 움직이는 장식을 없앰. 홈 타일을 누리과정 5개 영역 색으로 묶고 선 아이콘으로 통일 | 2.1 |
| 음성 | 미리 녹음한 **고품질 음성**을 먼저 쓰고, 없는 문장만 기기 음성 합성으로 읽는 구조. 앱이 말하는 문장 약 940개를 자동 목록(voice/lines.txt)으로 만들고 Genspark CLI로 생성하는 스크립트 제공. 음성 파일 자체는 보호자 PC에서 생성해야 함 | 2.1 |
| 보호자 | 성장 기록을 점수 대신 **'할 수 있게 된 것 / 연습하고 있는 것'**으로 표시(첫 시도 10문항 이상·정답률 75% 이상이면 '할 수 있게 된 것'). 영역별 놀이 횟수, 오늘 놀이 시간과 WHO 권고 안내 | 2.1 |
| 개인화 | 아이 이름을 부르며 인사("안녕, ○○아!"). 이름은 기기 안에만 저장 | — |

### 4.2 다음 단계 로드맵

- **한글**
  - 음절 박수 놀이(고-양-이)와 음절 합성("호…랑…이" 듣고 그림 고르기)
  - 만 5세 음절 탈락("호랑이에서 '호'를 빼면?")
  - 가획 비교 놀이(ㄱ→ㅋ)
  - 따라 쓰기 단계화: 획순 시범 → 점선 → 보고 가린 뒤 쓰기 → 자유 쓰기
  - 받침은 선택형 심화로 다루기
- **수학**
  - "○개 주세요"(수량 만들기) 과제
  - 규칙 찾기의 **패턴 추상화**("같은 규칙을 다른 그림으로 만들기")와 박자 명칭("짝-쿵")
  - 비전형 도형(회전·길쭉한 삼각형)과 "진짜 삼각형 찾기"
  - 공간 언어 피드백("꼭짓점 3개!")
  - 숫자 크기 비교
  - 짝꿍 카드 수 연결 모드(점 배열 ↔ 숫자 ↔ 손가락)
- **정서**
  - 하루 시작의 감정 체크인(보상 없음, 세기를 '조금/많이'로 표현)
  - 감정을 고른 뒤 행동을 고르는 단계("친구에게 어떻게 해 줄까?")
  - 만 5세 복합정서(채점하지 않음)
- **실행기능·신체**
  - 쑥쑥 체조에 "음악 멈추면 얼음"과 동작 순서 기억 추가
  - 멈춰! 놀이를 화면 밖 동작으로 하는 보호자 판정 모드
  - 실로폰 박자·리듬 따라치기 모드
- **공통**
  - 규칙 찾기·모양·짝꿍 카드에도 두 번 틀린 뒤의 단계적 힌트 넣기
  - 녹음 음성을 사람 목소리로 바꾼 조건과 합성 음성 조건 비교

### 4.3 효과 검증 제안(현장 적용 연구용)

- **독립적인 측정 도구를 쓰세요.** 앱 안의 정답률은 효과를 부풀립니다(Kim et al., 2021). 예를 들면 다음과 같습니다.
  - 기수 원리: Give-N 과제
  - 억제: HTKS 과제
  - 정서 지식: 표정 명명 과제
  - 한글: CV 음절 읽기 과제
- **설계:** 기관 단위로 무작위 배정하고, 사전·사후·추적(8~12주) 검사를 합니다. 투입량은 주 3회, 회당 15~20분 정도가 적당합니다(박현영·채수은, 2022; Wang et al., 2016 참고).
- **하위 비교:** 부모와 함께 쓰는 조건과 혼자 쓰는 조건, 과정 칭찬과 일반 칭찬 문구를 비교합니다(A/B).
- **윤리:** 인터넷 연결 없이 동작하는 앱이므로 기록은 기기 안에만 남습니다. 연구용 수집이 필요하면 보호자 동의를 받고 익명으로 따로 내보내는 기능을 설계해야 합니다.

---

## 5. 참고문헌

> ✔ PubMed 메타데이터(PMID) 또는 DOI 확인 · ◇ 웹 검색 결과 페이지로 서지 확인

### 5.1 교육용 앱·디지털 미디어
- ✔ AAP Council on Communications and Media (2016). Media and young minds. *Pediatrics*, 138(5), e20162591. doi:10.1542/peds.2016-2591 (PMID 27940793)
- ◇ Bus, A. G., Kucirkova, N., Ten Braak, D., & Ciesielska, M. (2025). Which interactive features in children's digital picture books promote reading comprehension? A meta-analysis. *Early Education and Development*. doi:10.1080/10409289.2025.2571978
- ◇ Callaghan, M. N., & Reich, S. M. (2018). Are educational preschool apps designed to teach? *Learning, Media and Technology*, 43(3), 280–293. doi:10.1080/17439884.2018.1498355
- ✔ Fisher, A. V., Godwin, K. E., & Seltman, H. (2014). Visual environment, attention allocation, and learning in young children. *Psychological Science*, 25(7), 1362–1370. doi:10.1177/0956797614533801 (PMID 24855019)
- ◇ Furenes, M. I., Kucirkova, N., & Bus, A. G. (2021). A comparison of children's reading on paper versus screen: A meta-analysis. *Review of Educational Research*, 91, 483–517. doi:10.3102/0034654321998074
- ✔ Griffith, S. F., et al. (2020). Apps as learning tools: A systematic review. *Pediatrics*, 145(1), e20191579. doi:10.1542/peds.2019-1579 (PMID 31871246)
- ◇ Hiniker, A., Suh, H., Cao, S., & Kientz, J. A. (2016). Screen time tantrums. *Proceedings of CHI '16*. doi:10.1145/2858036.2858278
- ✔ Hirsh-Pasek, K., et al. (2015). Putting education in "educational" apps. *Psychological Science in the Public Interest*, 16(1), 3–34. doi:10.1177/1529100615569721 (PMID 25985468)
- ◇ Huntington, B., Goulding, J., & Pitchford, N. J. (2023). Pedagogical features of interactive apps for effective learning of foundational skills. *British Journal of Educational Technology*. doi:10.1111/bjet.13317
- ✔ Jing, M., et al. (2023). Screen media exposure and young children's vocabulary learning and development: A meta-analysis. *Child Development*, 94(5), 1398–1418. doi:10.1111/cdev.13927 (PMID 37042116)
- ◇ Kim, J., Gilbert, J., Yu, Q., & Gale, C. (2021). Measures matter: A meta-analysis of the effects of educational apps on preschool to grade 3 children's literacy and math skills. *AERA Open*, 7. doi:10.1177/23328584211004183
- ✔ Kirkorian, H. L., Choi, K., & Pempek, T. A. (2016). Toddlers' word learning from contingent and noncontingent video on touch screens. *Child Development*, 87(2), 405–413. doi:10.1111/cdev.12508 (PMID 27018327)
- ✔ Kory Westlund, J. M., et al. (2017). Flat vs. expressive storytelling: Young children's learning and retention of a social robot's narrative. *Frontiers in Human Neuroscience*, 11, 295. doi:10.3389/fnhum.2017.00295 (PMID 28638330)
- ✔ Madigan, S., et al. (2020). Associations between screen use and child language skills. *JAMA Pediatrics*, 174(7), 665–675. doi:10.1001/jamapediatrics.2020.0327 (PMID 32202633)
- ◇ Mathers, S. J., et al. (2025). Features of digital media which influence social interactions between adults and children aged 2–7 years during joint media engagement: A multi-level meta-analysis. *Educational Research Review*.
- ✔ McArthur, B. A., et al. (2022). Global prevalence of meeting screen time guidelines among children 5 years and younger. *JAMA Pediatrics*, 176(4), 373–383. doi:10.1001/jamapediatrics.2021.6386 (PMID 35157028)
- ✔ Meyer, M., et al. (2019). Advertising in young children's apps. *Journal of Developmental & Behavioral Pediatrics*, 40(1), 32–39. doi:10.1097/DBP.0000000000000622 (PMID 30371646)
- ◇ Meyer, M., Zosh, J. M., et al. (2021). How educational are "educational" apps for young children? App store content analysis using the Four Pillars of Learning framework. *Journal of Children and Media*.
- ✔ Munzer, T. G., et al. (2019). Differences in parent-toddler interactions with electronic versus print books. *Pediatrics*, 143(4), e20182012. doi:10.1542/peds.2018-2012 (PMID 30910918)
- ✔ Outhwaite, L. A., et al. (2019). Raising early achievement in math with interactive apps: A randomized control trial. *Journal of Educational Psychology*, 111(2), 284–298. doi:10.1037/edu0000286 (PMID 30774149)
- ◇ Outhwaite, L. A., Early, E., Herodotou, C., & Van Herwegen, J. (2023). Understanding how educational maths apps can enhance learning. *British Journal of Educational Technology*, 54, 1292–1313. doi:10.1111/bjet.13339
- ◇ Patchan, M. M., & Puranik, C. S. (2016). Using tablet computers to teach preschool children to write letters. *Computers & Education*, 102, 128–137. doi:10.1016/j.compedu.2016.07.007
- ✔ Radesky, J., et al. (2022). Prevalence and characteristics of manipulative design in mobile applications used by children. *JAMA Network Open*, 5(6), e2217641. doi:10.1001/jamanetworkopen.2022.17641 (PMID 35713902)
- ✔ Raudenbush, S. W., et al. (2020). Longitudinal effects of an early-childhood intervention using adaptive assessment. *PNAS*, 117(45), 27945–27953. doi:10.1073/pnas.2002883117 (PMID 33106414)
- ◇ Sundararajan, N., & Adesope, O. (2020). Keep it coherent: A meta-analysis of the seductive details effect. *Educational Psychology Review*, 32, 707–734. doi:10.1007/s10648-020-09522-4
- ✔ Takacs, Z. K., Swart, E. K., & Bus, A. G. (2015). Benefits and pitfalls of multimedia and interactive features in technology-enhanced storybooks: A meta-analysis. *Review of Educational Research*, 85(4), 698–739. doi:10.3102/0034654314566989 (PMID 26640299)
- ◇ Taylor, G., Sala, G., Kolak, J., Gerhardstein, P., & Lingwood, J. (2024). Does adult-child co-use during digital media use improve children's learning aged 0–6 years? *Educational Research Review*, 44, 100614.
- ◇ WHO (2019). Guidelines on physical activity, sedentary behaviour and sleep for children under 5 years of age. ISBN 9789241550536.
- ✔ Xu, Y., et al. (2022). Dialogue with a conversational agent promotes children's story comprehension via enhancing engagement. *Child Development*, 93(2), e149–e167. doi:10.1111/cdev.13708 (PMID 34748214)

### 5.2 유아 수학
- ✔ Berkowitz, T., et al. (2015). Math at home adds up to achievement in school. *Science*, 350, 196–198. doi:10.1126/science.aac7427 (PMID 26450209)
- ✔ Bower, C., et al. (2020). Piecing together the role of a spatial assembly intervention in preschoolers' spatial and mathematics learning. *Developmental Psychology*, 56(4), 686–698. doi:10.1037/dev0000899 (PMID 32134293)
- ◇ Clements, D. H., & Sarama, J. (2008). Experimental evaluation of the effects of a research-based preschool mathematics curriculum. *American Educational Research Journal*, 45, 443–494. doi:10.3102/0002831207312908
- ◇ Clements, D. H., et al. (2011). Mathematics learned by young children in an intervention based on learning trajectories. *Journal for Research in Mathematics Education*, 42(2), 127–166.
- ◇ Clements, D. H., et al. (1999). Young children's concepts of shape. *Journal for Research in Mathematics Education*, 30(2), 192–212.
- ✔ Daucourt, M. C., et al. (2021). The home math environment and math achievement: A meta-analysis. *Psychological Bulletin*, 147(6), 565–596. doi:10.1037/bul0000330 (PMID 34843299)
- ✔ Duncan, G. J., et al. (2007). School readiness and later achievement. *Developmental Psychology*, 43(6), 1428–1446. doi:10.1037/0012-1649.43.6.1428 (PMID 18020822)
- ✔ Fisher, K. R., et al. (2013). Taking shape: Supporting preschoolers' acquisition of geometric knowledge through guided play. *Child Development*, 84(6), 1872–1878. doi:10.1111/cdev.12091 (PMID 23534446)
- ✔ Fyfe, E. R., et al. (2015). Easy as ABCABC: Abstract language facilitates performance on a concrete patterning task. *Child Development*, 86(3), 927–935. doi:10.1111/cdev.12331 (PMID 25571776)
- ✔ Gasteiger, H., et al. (2021). Fostering early numerical competencies by playing conventional board games. *Journal of Experimental Child Psychology*, 204, 105060. doi:10.1016/j.jecp.2020.105060 (PMID 33401161)
- ✔ Gunderson, E. A., & Levine, S. C. (2011). Some types of parent number talk count more than others. *Developmental Science*, 14(5), 1021–1032. doi:10.1111/j.1467-7687.2011.01050.x (PMID 21884318)
- ✔ Hawes, Z. C. K., et al. (2022). Effects of spatial training on mathematics performance: A meta-analysis. *Developmental Psychology*, 58(1), 112–137. doi:10.1037/dev0001281 (PMID 35073120)
- ✔ Kreilinger, I. L., et al. (2021). Mastery of structured quantities like finger or dice patterns predict arithmetic performance. *Cognitive Processing*, 22(1), 93–104. doi:10.1007/s10339-020-00994-4 (PMID 33021730)
- ✔ Laski, E. V., & Siegler, R. S. (2014). Learning from number board games: You learn what you encode. *Developmental Psychology*, 50(3), 853–864. doi:10.1037/a0034321 (PMID 24099546)
- ✔ Levine, S. C., et al. (2012). Early puzzle play: A predictor of preschoolers' spatial transformation skill. *Developmental Psychology*, 48(2), 530–542. doi:10.1037/a0025913 (PMID 22040312)
- ◇ Nelson, G., et al. (2024). A meta-analysis and quality review of mathematics interventions conducted in informal learning environments with caregivers and children. *Review of Educational Research*. doi:10.3102/00346543231156182
- ◇ Nelson, G., et al. (2025). Investigating main effects and moderators of linear number board games: A meta-analytic review. *Review of Educational Research*. doi:10.3102/00346543251383552
- ◇ Paliwal, V., & Baroody, A. J. (2018). How best to teach the cardinality principle? *Early Childhood Research Quarterly*, 44, 152–160. doi:10.1016/j.ecresq.2018.03.012
- ✔ Park, J., et al. (2016). Non-symbolic approximate arithmetic training improves math performance in preschoolers. *Journal of Experimental Child Psychology*, 152, 278–293. doi:10.1016/j.jecp.2016.07.011 (PMID 27596808)
- ✔ Ramani, G. B., & Siegler, R. S. (2008). Promoting broad and stable improvements in low-income children's numerical knowledge through playing number board games. *Child Development*, 79(2), 375–394. doi:10.1111/j.1467-8624.2007.01131.x (PMID 18366429)
- ✔ Rittle-Johnson, B., et al. (2017). Early math trajectories: Low-income children's mathematics knowledge from ages 4 to 11. *Child Development*, 88(5), 1727–1742. doi:10.1111/cdev.12662 (PMID 27921305)
- ◇ Satlow, E., & Newcombe, N. (1998). When is a triangle not a triangle? *Cognitive Development*, 13(4), 547–559.
- ✔ Schneider, M., et al. (2017). Associations of non-symbolic and symbolic numerical magnitude processing with mathematical competence: A meta-analysis. *Developmental Science*, 20(3). doi:10.1111/desc.12372 (PMID 26768176)
- ✔ Schneider, M., et al. (2018). Associations of number line estimation with mathematical competence: A meta-analysis. *Child Development*, 89(5), 1467–1484. doi:10.1111/cdev.13068 (PMID 29637540)
- ✔ Siegler, R. S., & Ramani, G. B. (2008). Playing linear numerical board games promotes low-income children's numerical development. *Developmental Science*, 11(5), 655–661. doi:10.1111/j.1467-7687.2008.00714.x (PMID 18801120)
- ◇ Siegler, R. S., & Ramani, G. B. (2009). Playing linear number board games—but not circular ones—improves low-income preschoolers' numerical understanding. *Journal of Educational Psychology*, 101(3), 545–560.
- ✔ Szkudlarek, E., et al. (2021). Failure to replicate the benefit of approximate arithmetic training for symbolic arithmetic fluency in adults. *Cognition*, 207, 104521. doi:10.1016/j.cognition.2020.104521 (PMID 33280814)
- ✔ Uttal, D. H., et al. (2013). The malleability of spatial skills: A meta-analysis of training studies. *Psychological Bulletin*, 139(2), 352–402. doi:10.1037/a0028446 (PMID 22663761)
- ◇ Wang, A. H., et al. (2016). Understanding the program effectiveness of early mathematics interventions for prekindergarten and kindergarten environments. *Early Education and Development*, 27(5), 692–713. doi:10.1080/10409289.2016.1116343
- ✔ Wijns, N., et al. (2021). Associations between repeating patterning, growing patterning, and numerical ability. *Child Development*, 92(4), 1354–1368. doi:10.1111/cdev.13490 (PMID 33398877)
- ✔ Zippert, E. L., et al. (2020). Finding patterns in objects and numbers: Repeating patterning in pre-K predicts kindergarten mathematics knowledge. *Journal of Experimental Child Psychology*, 200, 104965. doi:10.1016/j.jecp.2020.104965 (PMID 32889302)
- ◇ Zippert, E., et al. (2021). Helping preschoolers learn math: The impact of emphasizing the patterns in objects and numbers. *Journal of Educational Psychology*. doi:10.1037/edu0000656

### 5.3 한글과 초기 문해
- ◇ Bara, F., Gentaz, E., Colé, P., & Sprenger-Charolles, L. (2004). The visuo-haptic and haptic exploration of letters increases the kindergarten-children's understanding of the alphabetic principle. *Cognitive Development*, 19, 433–449.
- ◇ Cho, J. R. (2009). Syllable and letter knowledge in early Korean Hangul reading. *Journal of Educational Psychology*, 101(4), 938–947.
- ◇ Cho, J. R. (2021). Relations of copying skills to Korean word reading and spelling. *Journal of Research in Reading*, 44, 247–263. doi:10.1111/1467-9817.12332
- ✔ Cho, J. R., & McBride, C. (2022). Different cognitive correlates of early learning of spelling in Korean. *Journal of Learning Disabilities*, 55(2), 138–153. doi:10.1177/0022219420978231 (PMID 33307946)
- ◇ Cho, J. R., & McBride-Chang, C. (2005). Correlates of Korean Hangul acquisition among kindergartners and second graders. *Scientific Studies of Reading*, 9(1), 3–16.
- ◇ Ehri, L. C., et al. (2001). Phonemic awareness instruction helps children learn to read. *Reading Research Quarterly*, 36(3), 250–287. doi:10.1598/RRQ.36.3.2
- ✔ James, K. H., & Engelhardt, L. (2012). The effects of handwriting experience on functional brain development in pre-literate children. *Trends in Neuroscience and Education*, 1(1), 32–42. doi:10.1016/j.tine.2012.08.001 (PMID 25541600)
- ✔ Ju, Y., Sambai, A., & Uno, A. (2022). The influence of orthographic units on Hangul reading. *Frontiers in Psychology*, 13, 797874. doi:10.3389/fpsyg.2022.797874 (PMID 35432141)
- ◇ Kim, Y. S. (2007). Phonological awareness and literacy skills in Korean: An examination of the unique role of body-coda units. *Applied Psycholinguistics*, 28, 67–93.
- ◇ Kim, Y. S. (2009). The foundation of literacy skills in Korean. *Reading and Writing*, 22, 907–931. doi:10.1007/s11145-008-9131-0
- ◇ Kim, Y. S., & Petscher, Y. (2011). Relations of emergent literacy skill development with conventional literacy skill development in Korean. *Reading and Writing*, 24, 635–656. doi:10.1007/s11145-010-9240-4
- ◇ Kim, Y. S., & Petscher, Y. (2013). Language general and specific factors in letter acquisition: Considering child and letter characteristics in Korean. *Reading and Writing*. doi:10.1007/s11145-012-9367-6
- ✔ Longcamp, M., Zerbato-Poudou, M. T., & Velay, J. L. (2005). The influence of writing practice on letter recognition in preschool children. *Acta Psychologica*, 119(1), 67–79. doi:10.1016/j.actpsy.2004.10.019 (PMID 15823243)
- ◇ McTigue, E. M., Solheim, O. J., Zimmer, W. K., & Uppstad, P. H. (2020). Critically reviewing GraphoGame across the world. *Reading Research Quarterly*, 55(1), 45–73. doi:10.1002/rrq.256
- ◇ Mol, S. E., Bus, A. G., de Jong, M. T., & Smeets, D. J. H. (2008). Added value of dialogic parent–child book readings: A meta-analysis. *Early Education and Development*, 19(1), 7–26. doi:10.1080/10409280701838603
- ◇ National Early Literacy Panel (2008). *Developing early literacy*.
- ◇ Noble, C., et al. (2019). The impact of shared book reading on children's language skills: A meta-analysis. *Educational Research Review*, 28, 100290. doi:10.1016/j.edurev.2019.100290
- ◇ Pae, H. K. (2011). Is Korean a syllabic alphabet or an alphabetic syllabary. *Writing Systems Research*, 3(2), 103–115. doi:10.1093/wsr/wsr002
- ✔ Piasta, S. B., & Wagner, R. K. (2010). Developing early literacy skills: A meta-analysis of alphabet learning and instruction. *Reading Research Quarterly*, 45(1), 8–38. doi:10.1598/RRQ.45.1.2 (PMID 20671801)
- ◇ Verhoeven, L., et al. (2020). The effectiveness of computer-assisted early literacy interventions: A meta-analysis. *Educational Research Review*, 30, 100325. doi:10.1016/j.edurev.2020.100325

### 5.4 사회정서·동기
- ✔ Bennett-Pierre, G., et al. (2024). Effects of praise and "easy" feedback on children's persistence. *Journal of Experimental Child Psychology*, 247, 106032. doi:10.1016/j.jecp.2024.106032 (PMID 39111151)
- ✔ Bierman, K. L., et al. (2008). Promoting academic and social-emotional school readiness: The Head Start REDI program. *Child Development*, 79(6), 1802–1817. doi:10.1111/j.1467-8624.2008.01227.x (PMID 19037951)
- ✔ Blewitt, C., et al. (2018). Social and emotional learning associated with universal curriculum-based interventions in early childhood education and care centers. *JAMA Network Open*, 1(8), e185727. doi:10.1001/jamanetworkopen.2018.5727 (PMID 30646283)
- ✔ Brummelman, E., et al. (2014). "That's not just beautiful—that's incredibly beautiful!": The adverse impact of inflated praise on children with low self-esteem. *Psychological Science*, 25(3), 728–735. doi:10.1177/0956797613514251 (PMID 24434235)
- ✔ Cai, et al. (2026). Promoting social and emotional learning in K-12 students through digital-based interventions. *BMC Psychology*, 14. doi:10.1186/s40359-026-04434-4 (PMID 41888902)
- ✔ Calvert, S. L., et al. (2020). Young children's mathematical learning from intelligent characters. *Child Development*, 91(5), 1491–1508. doi:10.1111/cdev.13341 (PMID 31745971)
- ✔ Cimpian, A., Arce, H. M. C., Markman, E. M., & Dweck, C. S. (2007). Subtle linguistic cues affect children's motivation. *Psychological Science*, 18(4), 314–316. doi:10.1111/j.1467-9280.2007.01896.x (PMID 17470255)
- ✔ Deci, E. L., Koestner, R., & Ryan, R. M. (1999). A meta-analytic review of experiments examining the effects of extrinsic rewards on intrinsic motivation. *Psychological Bulletin*, 125(6), 627–668. doi:10.1037/0033-2909.125.6.627 (PMID 10589297)
- ✔ Denham, S. A., et al. (2003). Preschool emotional competence: Pathway to social competence? *Child Development*, 74(1), 238–256. doi:10.1111/1467-8624.00533 (PMID 12625448)
- ✔ Denham, S. A., et al. (2013). Preschoolers' social information processing and early school success: The challenging situations task. *British Journal of Developmental Psychology*, 31(2), 180–197. doi:10.1111/j.2044-835X.2012.02085.x (PMID 23659890)
- ✔ Domitrovich, C. E., Cortes, R. C., & Greenberg, M. T. (2007). Improving young children's social and emotional competence: A randomized trial of the preschool "PATHS" curriculum. *Journal of Primary Prevention*, 28(2), 67–91. doi:10.1007/s10935-007-0081-0 (PMID 17265130)
- ✔ Durlak, J. A., et al. (2011). The impact of enhancing students' social and emotional learning. *Child Development*, 82(1), 405–432. doi:10.1111/j.1467-8624.2010.01564.x (PMID 21291449)
- ✔ Gunderson, E. A., et al. (2013). Parent praise to 1- to 3-year-olds predicts children's motivational frameworks 5 years later. *Child Development*, 84(5), 1526–1541. doi:10.1111/cdev.12064 (PMID 23397904)
- ✔ Gunderson, E. A., et al. (2018). Parent praise to toddlers predicts fourth grade academic achievement via children's incremental mindsets. *Developmental Psychology*, 54(3), 397–409. doi:10.1037/dev0000444 (PMID 29172567)
- ✔ Havighurst, S. S., et al. (2010). Tuning in to Kids: Improving emotion socialization practices in parents of preschool children. *Journal of Child Psychology and Psychiatry*, 51(12), 1342–1350. doi:10.1111/j.1469-7610.2010.02303.x (PMID 20735794)
- ✔ Henderlong, J., & Lepper, M. R. (2002). The effects of praise on children's intrinsic motivation. *Psychological Bulletin*, 128(5), 774–795. doi:10.1037/0033-2909.128.5.774 (PMID 12206194)
- ✔ Izard, C., et al. (2001). Emotion knowledge as a predictor of social behavior and academic competence in children at risk. *Psychological Science*, 12(1), 18–23. doi:10.1111/1467-9280.00304 (PMID 11294223)
- ✔ Kamins, M. L., & Dweck, C. S. (1999). Person versus process praise and criticism. *Developmental Psychology*, 35(3), 835–847. doi:10.1037/0012-1649.35.3.835 (PMID 10380873)
- ◇ Lepper, M. R., Greene, D., & Nisbett, R. E. (1973). Undermining children's intrinsic interest with extrinsic reward. *Journal of Personality and Social Psychology*, 28(1), 129–137. doi:10.1037/h0035519
- ✔ Metcalfe, J. (2017). Learning from errors. *Annual Review of Psychology*, 68, 465–489. doi:10.1146/annurev-psych-010416-044022 (PMID 27648988)
- ✔ Patall, E. A., Cooper, H., & Robinson, J. C. (2008). The effects of choice on intrinsic motivation and related outcomes: A meta-analysis. *Psychological Bulletin*, 134(2), 270–300. doi:10.1037/0033-2909.134.2.270 (PMID 18298272)
- ◇ Pons, F., Harris, P. L., & de Rosnay, M. (2004). Emotion comprehension between 3 and 11 years. *European Journal of Developmental Psychology*, 1(2), 127–152. doi:10.1080/17405620344000022
- ◇ Rasmussen, E. E., et al. (2019). Promoting preschoolers' emotional competence through prosocial TV and mobile app use. *Media Psychology*, 22(1), 1–22. doi:10.1080/15213269.2018.1476890
- ✔ Ratcliff, K. A., et al. (2021). Longitudinal changes in young children's strategy use for emotion regulation. *Developmental Psychology*, 57(9), 1471–1486. doi:10.1037/dev0001235 (PMID 34929092)
- ◇ Schneider, M. R. (1974). Turtle technique in the classroom. *Teaching Exceptional Children*, 7, 21–24. doi:10.1177/004005997400700112
- ✔ Shi, J., Cheung, A. C. K., & Ni, A. (2022). The effectiveness of Promoting Alternative Thinking Strategies program: A meta-analysis. *Frontiers in Psychology*, 13, 1030572. doi:10.3389/fpsyg.2022.1030572 (PMID 36571043)
- ✔ Smith, J. P., Glass, D. J., & Fireman, G. (2015). The understanding and experience of mixed emotions in 3-5-year-old children. *Journal of Genetic Psychology*, 176(1–2), 65–81. doi:10.1080/00221325.2014.1002750 (PMID 25695201)
- ✔ Trentacosta, C. J., & Fine, S. E. (2010). Emotion knowledge, social competence, and behavior problems in childhood and adolescence: A meta-analytic review. *Social Development*, 19(1), 1–29. doi:10.1111/j.1467-9507.2009.00543.x (PMID 21072259)
- ✔ Webster-Stratton, C., Reid, M. J., & Stoolmiller, M. (2008). Preventing conduct problems and improving school readiness: Evaluation of the Incredible Years. *Journal of Child Psychology and Psychiatry*, 49(5), 471–488. doi:10.1111/j.1469-7610.2007.01861.x (PMID 18221346)
- ✔ Widen, S. C., & Russell, J. A. (2003). A closer look at preschoolers' freely produced labels for facial expressions. *Developmental Psychology*, 39(1), 114–128. doi:10.1037/0012-1649.39.1.114 (PMID 12518813)
- ✔ Zentall, S. R., & Morris, B. J. (2010). "Good job, you're so smart": The effects of inconsistency of praise type on young children's motivation. *Journal of Experimental Child Psychology*, 107(2), 155–163. doi:10.1016/j.jecp.2010.04.015 (PMID 20570281)

### 5.5 실행기능·신체활동·음악·미술·눈 건강
- ✔ Baier, J., Wöllner, C., & Wolf, A. (2021). Interpersonal musical synchronization and prosocial behavior in children. *Frontiers in Psychology*, 12, 784255. doi:10.3389/fpsyg.2021.784255 (PMID 34956007)
- ✔ Bentley, L. A., et al. (2023). A translational application of music for preschool cognitive development: RCT. *Developmental Science*, 26, e13358. doi:10.1111/desc.13358 (PMID 36511452)
- ✔ Blair, C., & Raver, C. C. (2014). Closing the achievement gap through modification of neurocognitive and neuroendocrine function. *PLoS One*, 9, e112393. doi:10.1371/journal.pone.0112393 (PMID 25389751)
- ✔ Cameron, C. E., et al. (2012). Fine motor skills and executive function both contribute to kindergarten achievement. *Child Development*, 83, 1229–1244. doi:10.1111/j.1467-8624.2012.01768.x (PMID 22537276)
- ✔ Degé, F., & Schwarzer, G. (2011). The effect of a music program on phonological awareness in preschoolers. *Frontiers in Psychology*, 2, 124. doi:10.3389/fpsyg.2011.00124 (PMID 21734895)
- ✔ Diamond, A., Barnett, W. S., Thomas, J., & Munro, S. (2007). Preschool program improves cognitive control. *Science*, 318, 1387–1388. doi:10.1126/science.1151148 (PMID 18048670)
- ✔ Diamond, A., & Lee, K. (2011). Interventions shown to aid executive function development in children 4 to 12 years old. *Science*, 333, 959–964. doi:10.1126/science.1204529 (PMID 21852486)
- ✔ Diamond, A., et al. (2019). Randomized control trial of Tools of the Mind. *PLoS One*, 14, e0222447. doi:10.1371/journal.pone.0222447 (PMID 31527919)
- ✔ Foreman, J., et al. (2021). Association between digital smart device use and myopia. *Lancet Digital Health*, 3, e806–e818. doi:10.1016/S2589-7500(21)00135-7 (PMID 34625399)
- ✔ Gerstadt, C. L., Hong, Y. J., & Diamond, A. (1994). The relationship between cognition and action: Performance of children 3½–7 years old on a Stroop-like day-night test. *Cognition*, 53, 129–153. doi:10.1016/0010-0277(94)90068-x (PMID 7805351)
- ✔ Gordon, R. L., Fehd, H. M., & McCandliss, B. D. (2015). Does music training enhance literacy skills? A meta-analysis. *Frontiers in Psychology*, 6, 1777. doi:10.3389/fpsyg.2015.01777 (PMID 26648880)
- ✔ He, M., et al. (2015). Effect of time spent outdoors at school on the development of myopia among children in China. *JAMA*, 314, 1142–1148. doi:10.1001/jama.2015.10803 (PMID 26372583)
- ✔ Kido, A., Miyake, M., & Watanabe, N. (2024). Interventions to increase time spent outdoors for preventing incidence and progression of myopia in children. *Cochrane Database of Systematic Reviews*, CD013549. doi:10.1002/14651858.CD013549.pub2 (PMID 38864362)
- ✔ Kirkorian, H. L., et al. (2020). Drawing across media: A cross-sectional experiment on preschoolers' drawings produced using traditional versus electronic mediums. *Developmental Psychology*, 56, 28–39. doi:10.1037/dev0000825 (PMID 31697094)
- ✔ Lee, E. Y., et al. (2024). Global trends in importance of 24-hour movement behaviors to pediatric health: Implications for South Korea. *Clinical and Experimental Pediatrics*, 68, 16–29. doi:10.3345/cep.2024.00178 (PMID 39533734)
- ✔ Li, L., et al. (2020). The effects of chronic physical activity interventions on executive functions in children aged 3–7 years. *Journal of Science and Medicine in Sport*, 23, 949–954. doi:10.1016/j.jsams.2020.03.007 (PMID 32360243)
- ✔ Lin, L. Y., Cherng, R. J., & Chen, Y. J. (2017). Effect of touch screen tablet use on fine motor development of young children. *Physical & Occupational Therapy in Pediatrics*, 37, 457–467. doi:10.1080/01942638.2016.1255290 (PMID 28071977)
- ✔ Liu, W., Wang, Z., Yi, J., & Li, X. (2026). The effects of physical activity on executive function in preschool children: A meta-analysis of RCTs. *Frontiers in Psychology*, 17, 1882118. doi:10.3389/fpsyg.2026.1882118 (PMID 42539512)
- ✔ Logan, S. W., et al. (2012). Getting the fundamentals of movement: A meta-analysis of the effectiveness of motor skill interventions in children. *Child: Care, Health and Development*, 38, 305–315. doi:10.1111/j.1365-2214.2011.01307.x (PMID 21880055)
- ✔ Mavilidi, M. F., et al. (2017). Immediate and delayed effects of integrating physical activity into preschool children's learning of numeracy skills. *Journal of Experimental Child Psychology*, 166, 502–519. doi:10.1016/j.jecp.2017.09.009 (PMID 29096234)
- ✔ Ponitz, C. C., et al. (2009). A structured observation of behavioral self-regulation and its contribution to kindergarten outcomes. *Developmental Psychology*, 45, 605–619. doi:10.1037/a0015365 (PMID 19413419)
- ✔ Sala, G., & Gobet, F. (2020). Cognitive and academic benefits of music training with children: A multilevel meta-analysis. *Memory & Cognition*, 48, 1429–1441. doi:10.3758/s13421-020-01060-2 (PMID 32728850)
- ✔ Scionti, N., et al. (2020). Is cognitive training effective for improving executive functions in preschoolers? *Frontiers in Psychology*, 10, 2812. doi:10.3389/fpsyg.2019.02812 (PMID 31998168)
- ✔ Song, H., et al. (2023). Do acute and chronic physical activity interventions affect the cognitive function of preschool children? *Psychology of Sport and Exercise*, 67, 102419. doi:10.1016/j.psychsport.2023.102419 (PMID 37665872)
- ✔ Takacs, Z. K., & Kassai, R. (2019). The efficacy of different interventions to foster children's executive function skills: A series of meta-analyses. *Psychological Bulletin*, 145, 653–697. doi:10.1037/bul0000195 (PMID 31033315)
- ✔ Talens-Estarelles, C., et al. (2023). The effects of breaks on digital eye strain, dry eye and binocular vision: Testing the 20-20-20 rule. *Contact Lens and Anterior Eye*, 46, 101744. doi:10.1016/j.clae.2022.101744 (PMID 35963776)
- ✔ Trujillo-Trujillo, C. C., et al. (2026). Effectiveness of executive function interventions in typically developing preschool children. *Child Neuropsychology*, 32, 598–632. doi:10.1080/09297049.2025.2595075 (PMID 41491942)
- ✔ Zelazo, P. D. (2006). The Dimensional Change Card Sort (DCCS): A method of assessing executive function in children. *Nature Protocols*, 1, 297–301. doi:10.1038/nprot.2006.46 (PMID 17406248)

### 5.6 국내 문헌 (KCI·국가기관 자료, 검색 결과로 서지 확인)
- 과학기술정보통신부·한국지능정보사회진흥원 (2026. 3. 26 발표). 2025년 스마트폰 과의존 실태조사. (유아동 과의존 위험군 26.0%; 2024년 25.9%, 2023년 25.0%)
- 교육부·보건복지부 (2019). 2019 개정 누리과정 해설서. https://repo.kicce.re.kr/handle/2019.oak/4997
- 김남윤(검색 결과에 따라 '김남연')·김민정 (2024). 유아 디지털 매체 활용 교육의 효과에 대한 메타분석. 컴퓨터교육학회 논문지, 27(6).
- 김보영 (2016). 유아의 실행기능, 자아존중감, 정서지능이 또래유능성에 미치는 영향. 육아지원연구, 11(3), 29–51.
- 김애화 (2012). 음운 인식 특성 연구. 학습장애연구, 9(2), 93–111.
- 김유나·윤혜주·권두순 (2025). 애플리케이션을 활용한 수학활동이 만 5세 유아의 놀이성과 수학적 성향 및 수학적 문제해결력에 미치는 효과. 어린이미디어연구, 24(3).
- 김은지·전귀연 (2020). 유아의 스마트미디어 이용이 인지와 언어 발달에 미치는 영향. Human Ecology Research, 58(1), 13–29.
- 박현영·채수은 (2022). 국내 사회정서학습(SEL) 프로그램 효과에 관한 메타분석. 인간발달연구, 29(1), 79–100.
- 설예림·김진경·박소연·강대혁 (2022). 스마트폰 어플리케이션을 이용한 박자 맞추기 게임이 발달 지연 아동의 실행기능에 미치는 효과. 재활치료과학, 11(3).
- 안성우·허민정 (2011). 4세–7세 유아들의 한글 낱자 지식과 자소-음소대응규칙 지식 발달 특성 연구. 특수아동교육연구, 13(2), 267–287.
- 이숙·김화수 (2014). 일반아동의 음절·음절체·각운·음소의 발달 특성. 언어치료연구, 23(1), 127–156.
- 이정원·박원순·엄지원 (2021). 영유아의 미디어 이용 적정화를 위한 정책방안 연구. 육아정책연구소.
- 이정화 (2015). 유아 수세기(counting)에 관한 국내 연구동향 분석. 한국보육지원학회지, 11(3).
- 임혜성·최진경·이민경 (2019). 부모의 유아 수학능력에 대한 기대 및 부모의 수학 태도와 유아의 수학능력의 관계: 부모의 수학적 상호작용의 매개효과. 유아교육연구, 39(6).
- 조하영·윤미승 (2022). 메타분석을 이용한 감정코칭 부모교육프로그램 연구의 체계적 리뷰. 유아교육연구, 42(2), 87–105. doi:10.18023/kjece.2022.42.2.004
- 최나야·이순형 (2007). 한글 자음과 모음에 대한 유아의 지식이 단어 읽기에 미치는 영향. 가정과삶의질연구, 25(3), 151–168.
- 한국언론진흥재단 (2023). 2023 어린이 미디어 이용 조사.
- 홍혜경·김세루 (2008). 유아의 기하능력과 도형의 합성 및 분할 능력에 관한 연구. 유아교육연구, 28(1), 143–158.

---

## 부록 B. 미확인 문헌

아래 문헌은 조사 중에 언급되었지만 서지나 결과를 끝까지 확인하지 못해 본문 근거로 쓰지 않았습니다.

**국외 문헌**
- Murano et al.(2020) 유아 SEL 메타분석
- Brackett et al.(2012) RULER RCT
- Al's Pals 평가 연구
- Second Step Early Learning 유아 RCT 결과
- Diamond(2013) Annual Review
- Bergman Nutley(2011)
- Schmitt(2015) Red Light Purple Light
- Clements et al.(2019) 도형 합성 학습 경로
- Papic(2011), Kidd(2013/2014) 패턴 중재
- Yoon, Bolger, Kwon & Perfetti(2002)
- Strouse & Troseth(2014)

**국내 문헌**
- KCI ART000874602(유아 수 세기에서 한자어 수 단어 세기가 고유어보다 높았다는 연구로 추정)
- 서울대 학위논문 「칭찬과 성패 상황에 따른 유아의 자부심과 수치심 및 과제지속수행」
- 대한의사협회의 연령별 화면 시간 권고
- 보건복지부 「한국인을 위한 신체활동 지침서」(2023) 개정판의 유아 항목

**공식 문서 원문**
- 누리과정 해설서의 문자교육 관련 원문 문구
- WHO·AAP 권고의 세부 문구

**직접 연구를 찾지 못한 주제**
- 획순 지도의 효과
- 한국어판 글자-소리 학습 게임의 효과
- 한국 유아 대상 선형 수 보드게임
