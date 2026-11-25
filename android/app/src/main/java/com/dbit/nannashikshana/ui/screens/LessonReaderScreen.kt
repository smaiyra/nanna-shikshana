package com.dbit.nannashikshana.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbit.nannashikshana.R
import com.dbit.nannashikshana.domain.model.AppLanguage
import com.dbit.nannashikshana.domain.model.LessonModule
import com.dbit.nannashikshana.ui.theme.NannaAmber
import com.dbit.nannashikshana.ui.theme.NannaBorder
import com.dbit.nannashikshana.ui.theme.NannaEmeraldDark
import com.dbit.nannashikshana.ui.theme.NannaEmeraldSoft
import com.dbit.nannashikshana.ui.theme.NannaRose
import com.dbit.nannashikshana.ui.theme.NannaRoseSoft
import com.dbit.nannashikshana.ui.theme.NannaSkyDark
import com.dbit.nannashikshana.ui.theme.NannaSkySoft
import com.dbit.nannashikshana.ui.theme.NannaSlate400
import com.dbit.nannashikshana.ui.theme.NannaSlate500
import com.dbit.nannashikshana.ui.theme.NannaSlate600
import com.dbit.nannashikshana.ui.theme.NannaSlate800
import com.dbit.nannashikshana.ui.theme.NannaSlate900
import com.dbit.nannashikshana.ui.theme.NannaSurface
import com.dbit.nannashikshana.ui.theme.NannaSurfaceMuted
import com.dbit.nannashikshana.ui.viewmodel.QuizUiState

@Composable
fun LessonReaderScreen(
    lesson: LessonModule,
    language: AppLanguage,
    quizState: QuizUiState,
    isAudioPlaying: Boolean,
    onBack: () -> Unit,
    onModeChange: (Boolean) -> Unit,
    onToggleAudio: () -> Unit,
    onSpeakVowel: (String, String) -> Unit,
    onChime: () -> Unit,
    onBuzz: () -> Unit,
    onSelectQuizOption: (Int) -> Unit,
    onConfirmQuizAnswer: () -> Unit,
    onAdvanceQuiz: () -> Unit,
    onRetakeQuiz: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back + Reader/Quiz toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(text = stringResource(R.string.back_to_dashboard), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    color = NannaSurfaceMuted,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(false to R.string.mode_reader, true to R.string.mode_quiz).forEach { (isQuiz, labelRes) ->
                            val selected = quizState.isQuizMode == isQuiz
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) NannaSurface else Color.Transparent)
                                    .clickable { onModeChange(isQuiz) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(labelRes),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) NannaSlate900 else NannaSlate600
                                )
                            }
                        }
                    }
                }
            }
        }

        // Lesson Header Card + Audio TTS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NannaSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, NannaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.KN) lesson.chapterKn else lesson.chapterEn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NannaSkyDark
                        )
                        Text(
                            text = "${lesson.grade} · ${lesson.fileSizeMb} MB",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NannaSlate500
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.KN) lesson.titleKn else lesson.titleEn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = NannaSlate900
                    )
                    Button(
                        onClick = onToggleAudio,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAudioPlaying) NannaAmber else NannaSkySoft,
                            contentColor = if (isAudioPlaying) Color.White else NannaSkyDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isAudioPlaying) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(if (isAudioPlaying) R.string.stop_audio else R.string.listen_kannada),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (!quizState.isQuizMode) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.interactive_diagram),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate600
                    )
                    when (lesson.diagramType) {
                        "fractions" -> FractionDiscModel(language = language, onChime = onChime)
                        "photosynthesis" -> PhotosynthesisModel(onChime = onChime, onBuzz = onBuzz)
                        "aksharamale" -> AksharamaleSoundboardModel(language = language, onSpeakVowel = onSpeakVowel)
                        else -> HoysalaBlueprintModel(onChime = onChime)
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NannaSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, NannaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = NannaSkyDark, modifier = Modifier.size(18.dp))
                            Text(
                                text = stringResource(R.string.lesson_concept),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NannaSlate900
                            )
                        }
                        lesson.contentSections.forEach { sec ->
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, NannaBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.KN) sec.headingKn else sec.headingEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NannaSlate900
                                    )
                                    Text(
                                        text = if (language == AppLanguage.KN) sec.bodyKn else sec.bodyEn,
                                        fontSize = 12.sp,
                                        color = NannaSlate600
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { onModeChange(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = NannaEmeraldDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Rounded.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.take_quiz_btn),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        } else {
            item {
                QuizCard(
                    lesson = lesson,
                    language = language,
                    quizState = quizState,
                    onSelectOption = onSelectQuizOption,
                    onConfirmAnswer = onConfirmQuizAnswer,
                    onAdvance = onAdvanceQuiz,
                    onRetake = onRetakeQuiz,
                    onFinish = onBack
                )
            }
        }
    }
}

@Composable
private fun QuizCard(
    lesson: LessonModule,
    language: AppLanguage,
    quizState: QuizUiState,
    onSelectOption: (Int) -> Unit,
    onConfirmAnswer: () -> Unit,
    onAdvance: () -> Unit,
    onRetake: () -> Unit,
    onFinish: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NannaSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, NannaBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (quizState.completed) {
            val total = lesson.quiz.size
            val perfect = quizState.score == total
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(NannaEmeraldSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 30.sp)
                }
                Text(
                    text = stringResource(R.string.quiz_completed),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NannaSlate900
                )
                Text(
                    text = stringResource(if (perfect) R.string.perfect_score else R.string.good_job),
                    fontSize = 12.sp,
                    color = NannaSlate500,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NannaBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.correct_answers_label),
                                fontSize = 11.sp,
                                color = NannaSlate500
                            )
                            Text(
                                text = "${quizState.score} / $total",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = NannaSlate800
                            )
                        }
                    }
                    Surface(
                        color = NannaEmeraldSoft,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.earn_total_xp),
                                fontSize = 11.sp,
                                color = NannaEmeraldDark
                            )
                            Text(
                                text = "+${quizState.score * 20} XP",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = NannaEmeraldDark
                            )
                        }
                    }
                }

                Button(
                    onClick = onFinish,
                    colors = ButtonDefaults.buttonColors(containerColor = NannaSkyDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(text = stringResource(R.string.finish_lesson), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRetake,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(text = stringResource(R.string.retake_quiz), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            val question = lesson.quiz[quizState.currentQuestionIndex]
            val options = if (language == AppLanguage.KN) question.optionsKn else question.optionsEn
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            R.string.quiz_question_progress,
                            quizState.currentQuestionIndex + 1,
                            lesson.quiz.size
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NannaSlate500
                    )
                    Text(
                        text = stringResource(R.string.quiz_pool, lesson.quiz.size * 20),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NannaEmeraldDark
                    )
                }
                HorizontalDivider(color = NannaSurfaceMuted)

                Text(
                    text = if (language == AppLanguage.KN) question.questionKn else question.questionEn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NannaSlate900
                )

                options.forEachIndexed { idx, opt ->
                    val isSelected = quizState.selectedOptionIndex == idx
                    val isCorrect = question.correctIndex == idx
                    val bgColor = when {
                        quizState.answerSubmitted && isCorrect -> NannaEmeraldSoft
                        quizState.answerSubmitted && isSelected && !isCorrect -> NannaRoseSoft
                        isSelected -> NannaSkySoft
                        else -> Color(0xFFF8FAFC)
                    }
                    val borderColor = when {
                        quizState.answerSubmitted && isCorrect -> NannaEmeraldDark
                        quizState.answerSubmitted && isSelected && !isCorrect -> NannaRose
                        isSelected -> NannaSkyDark
                        else -> NannaBorder
                    }

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !quizState.answerSubmitted) { onSelectOption(idx) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = NannaSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, NannaBorder),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + idx).toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NannaSlate600
                                        )
                                    }
                                }
                                Text(
                                    text = opt,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected || (quizState.answerSubmitted && isCorrect)) FontWeight.Bold else FontWeight.Medium,
                                    color = NannaSlate900
                                )
                            }
                            if (quizState.answerSubmitted && isCorrect) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NannaEmeraldDark, modifier = Modifier.size(18.dp))
                            } else if (quizState.answerSubmitted && isSelected && !isCorrect) {
                                Icon(Icons.Rounded.Cancel, contentDescription = null, tint = NannaRose, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (quizState.answerSubmitted) {
                    val isRight = quizState.selectedOptionIndex == question.correctIndex
                    Surface(
                        color = if (isRight) NannaEmeraldSoft else NannaRoseSoft,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (isRight) Color(0xFFA7F3D0) else Color(0xFFFECDD3)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(if (isRight) R.string.correct_answer else R.string.incorrect_answer),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRight) NannaEmeraldDark else NannaRose
                            )
                            Text(
                                text = if (language == AppLanguage.KN) question.explanationKn else question.explanationEn,
                                fontSize = 11.sp,
                                color = NannaSlate800
                            )
                        }
                    }

                    Button(
                        onClick = onAdvance,
                        colors = ButtonDefaults.buttonColors(containerColor = NannaEmeraldDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = stringResource(
                                if (quizState.currentQuestionIndex < lesson.quiz.lastIndex) R.string.next_question else R.string.see_results
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = onConfirmAnswer,
                        enabled = quizState.selectedOptionIndex != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NannaSkyDark,
                            disabledContainerColor = NannaSurfaceMuted,
                            disabledContentColor = NannaSlate400
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.submit_answer),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
