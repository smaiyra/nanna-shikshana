import hashlib
import json
from pathlib import Path
import zipfile

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.config import settings
from app.models.content import ContentPack, LessonModule
from app.models.gamification import Badge, DailyQuest, LeaderboardPeer

LESSONS_SEED = [
    {
        "lesson_key": "math_fractions_ch3",
        "pack_code": "pack_math_fractions_ch3",
        "subject": "math",
        "grade": "Grade 6 KTBS",
        "version": 1,
        "sort_order": 1,
        "file_size_mb": 0.8,
        "package_name": "KTBS_Math_Fractions_G6.pkg.zip",
        "title_en": "Fractions: Parts & Wholes",
        "title_kn": "ಅಪೂರ್ಣಾಂಕಗಳು: ಭಾಗಗಳು ಮತ್ತು ಪೂರ್ಣ",
        "chapter_en": "Chapter 3 · Mathematics",
        "chapter_kn": "ಅಧ್ಯಾಯ ೩ · ಗಣಿತ",
        "description_en": "Master fractional slices, numerators, and denominators through visual fraction discs and real-world roti cutting.",
        "description_kn": "ರೋಟಿ ಮತ್ತು ಚಕ್ರದ ತುಂಡುಗಳ ಮೂಲಕ ಅಂಶ ಮತ್ತು ಛೇದಗಳ ಸಂಕಲ್ಪನೆಯನ್ನು ದೃಶ್ಯರೂಪದಲ್ಲಿ ಸುಲಭವಾಗಿ ಕಲಿಯಿರಿ.",
        "diagram_type": "fractions",
        "audio_text_kn": "ಶಭಾಷ್! ಅಪೂರ್ಣಾಂಕ ಎಂದರೆ ಒಂದು ಪೂರ್ಣ ವಸ್ತುವಿನ ಸಮಾನ ಭಾಗಗಳು. ಮೇಲಿನ ಸಂಖ್ಯೆ ಅಂಶ, ಕೆಳಗಿನ ಸಂಖ್ಯೆ ಛೇದ.",
        "audio_text_en": "Bravo! A fraction represents equal parts of a whole object. The top number is the numerator, and the bottom is the denominator.",
        "content_sections": [
            {
                "heading_en": "1. What is a Fraction?",
                "heading_kn": "೧. ಅಪೂರ್ಣಾಂಕ ಎಂದರೇನು?",
                "body_en": "When a whole roti or circle is divided into equal slices, each slice is a fraction. For example, 1/2 means one part out of two equal halves.",
                "body_kn": "ಒಂದು ಪೂರ್ಣ ರೊಟ್ಟಿ ಅಥವಾ ವೃತ್ತವನ್ನು ಸಮಾನ ಭಾಗಗಳಾಗಿ ಹಂಚಿದಾಗ, ಪ್ರತಿಯೊಂದು ಭಾಗವನ್ನೂ ಅಪೂರ್ಣಾಂಕ ಎನ್ನುತ್ತೇವೆ. ಉದಾಹರಣೆಗೆ ೧/೨ ಎಂದರೆ ಎರಡು ಸಮಭಾಗಗಳಲ್ಲಿ ಒಂದು ಭಾಗ.",
            },
            {
                "heading_en": "2. Numerator vs Denominator",
                "heading_kn": "೨. ಅಂಶ ಮತ್ತು ಛೇದ",
                "body_en": "The Denominator (bottom) tells how many equal parts make the whole. The Numerator (top) counts how many parts we are considering.",
                "body_kn": "ಛೇದವು (ಕೆಳಗಿನ ಸಂಖ್ಯೆ) ಒಟ್ಟು ಸಮಭಾಗಗಳ ಸಂಖ್ಯೆಯನ್ನು ಸೂಚಿಸುತ್ತದೆ. ಅಂಶವು (ಮೇಲಿನ ಸಂಖ್ಯೆ) ನಾವು ಆಯ್ದುಕೊಂಡ ಭಾಗಗಳನ್ನು ತಿಳಿಸುತ್ತದೆ.",
            },
        ],
        "quiz": [
            {
                "id": "q_frac_1",
                "question_en": "If a circle is divided into 4 equal slices and 3 are shaded, what fraction is shaded?",
                "question_kn": "ಒಂದು ವೃತ್ತವನ್ನು ೪ ಸಮಭಾಗಗಳನ್ನಾಗಿ ಮಾಡಿ ೩ ಭಾಗಗಳಿಗೆ ಬಣ್ಣ ಹಚ್ಚಿದರೆ, ಆ ಭಾಗದ ಅಪೂರ್ಣಾಂಕ ಎಷ್ಟು?",
                "options_en": ["1/4", "3/4", "4/3", "2/4"],
                "options_kn": ["೧/೪", "೩/೪", "೪/೩", "೨/೪"],
                "correct_index": 1,
                "explanation_en": "3 parts are taken out of 4 total equal slices, so the fraction is 3/4.",
                "explanation_kn": "ಒಟ್ಟು ೪ ಸಮಭಾಗಗಳಲ್ಲಿ ೩ ಭಾಗಗಳನ್ನು ಆರಿಸಿಕೊಳ್ಳಲಾಗಿದೆ, ಆದ್ದರಿಂದ ೩/೪.",
            },
            {
                "id": "q_frac_2",
                "question_en": "In the fraction 2/5, what is the Denominator (ಛೇದ)?",
                "question_kn": "೨/೫ ಅಪೂರ್ಣಾಂಕದಲ್ಲಿ ಛೇದ ಯಾವುದು?",
                "options_en": ["2", "5", "7", "10"],
                "options_kn": ["೨", "೫", "೭", "೧೦"],
                "correct_index": 1,
                "explanation_en": "The bottom number is the denominator, which is 5.",
                "explanation_kn": "ಅಪೂರ್ಣಾಂಕದ ಕೆಳಗಿನ ಸಂಖ್ಯೆ ಛೇದ, ಅಂದರೆ ೫.",
            },
            {
                "id": "q_frac_3",
                "question_en": "Which fraction represents exactly one half of a whole?",
                "question_kn": "ಯಾವ ಅಪೂರ್ಣಾಂಕವು ನಿಖರವಾಗಿ ಅರ್ಧ ಭಾಗವನ್ನು ಸೂಚಿಸುತ್ತದೆ?",
                "options_en": ["2/4", "1/3", "3/5", "1/4"],
                "options_kn": ["೨/೪", "೧/೩", "೩/೫", "೧/೪"],
                "correct_index": 0,
                "explanation_en": "2/4 simplifies directly to 1/2 (half).",
                "explanation_kn": "೨/೪ ಅನ್ನು ಸರಳೀಕರಿಸಿದರೆ ೧/೨ (ಅರ್ಧ) ಆಗುತ್ತದೆ.",
            },
        ],
    },
    {
        "lesson_key": "sci_photosynthesis_ch5",
        "pack_code": "pack_sci_photosynthesis_ch5",
        "subject": "science",
        "grade": "Grade 7 KTBS",
        "version": 1,
        "sort_order": 2,
        "file_size_mb": 0.9,
        "package_name": "KTBS_Sci_Photosynthesis_G7.pkg.zip",
        "title_en": "Photosynthesis: Plant Food Factory",
        "title_kn": "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆ: ಸಸ್ಯಗಳ ಆಹಾರ ಕಾರ್ಖಾನೆ",
        "chapter_en": "Chapter 5 · Science",
        "chapter_kn": "ಅಧ್ಯಾಯ ೫ · ವಿಜ್ಞಾನ",
        "description_en": "Discover how green leaves use solar energy, chlorophyll, carbon dioxide, and root water to produce glucose and release life-giving oxygen.",
        "description_kn": "ಕ್ಲೋರೋಫಿಲ್, ಸೂರ್ಯನ ಬೆಳಕು, ನೀರಿನಂಶ ಮತ್ತು ಇಂಗಾಲದ ಡೈಆಕ್ಸೈಡ್ ಬಳಸಿ ಗಿಡಗಳು ಆಹಾರ ತಯಾರಿಸುವ ಅದ್ಭುತ ಪ್ರಕ್ರಿಯೆ ತಿಳಿಯಿರಿ.",
        "diagram_type": "photosynthesis",
        "audio_text_kn": "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆಯಲ್ಲಿ ಹಸಿರು ಎಲೆಗಳು ಸೂರ್ಯನ ಬೆಳಕು ಮತ್ತು ನೀರಿನ ಸಹಾಯದಿಂದ ಗ್ಲುಕೋಸ್ ತಯಾರಿಸಿ ನಮಗೆ ಆಮ್ಲಜನಕವನ್ನು ನೀಡುತ್ತವೆ.",
        "audio_text_en": "In photosynthesis, green leaves use sunlight and water to produce glucose and release oxygen for all living beings.",
        "content_sections": [
            {
                "heading_en": "1. The Solar Recipe",
                "heading_kn": "೧. ಸಸ್ಯಗಳ ಆಹಾರ ತಯಾರಿಕೆ",
                "body_en": "Chlorophyll in the chloroplasts absorbs sunlight. Roots absorb water (H2O), while stomata beneath the leaf absorb Carbon Dioxide (CO2).",
                "body_kn": "ಎಲೆಗಳಲ್ಲಿನ ಪತ್ರಹರಿತ್ತು (Chlorophyll) ಸೂರ್ಯನ ಬೆಳಕನ್ನು ಹೀರುತ್ತದೆ. ಬೇರುಗಳು ನೀರನ್ನು ಹೀರಿ, ಎಲೆಯ ರಂಧ್ರಗಳು ಗಾಳಿಯಿಂದ ಇಂಗಾಲದ ಡೈಆಕ್ಸೈಡ್ ಪಡೆಯುತ್ತವೆ.",
            },
            {
                "heading_en": "2. Output: Glucose & Oxygen",
                "heading_kn": "೨. ಫಲಿತಾಂಶ: ಗ್ಲುಕೋಸ್ ಮತ್ತು ಆಮ್ಲಜನಕ",
                "body_en": "The chemical reaction converts these raw ingredients into Glucose (stored energy for the plant) and releases Oxygen (O2) into the atmosphere.",
                "body_kn": "ಈ ರಾಸಾಯನಿಕ ಪ್ರಕ್ರಿಯೆಯಿಂದ ಸಸ್ಯಕ್ಕೆ ಗ್ಲುಕೋಸ್ ಸಿಗುತ್ತದೆ ಮತ್ತು ವಾತಾವರಣಕ್ಕೆ ಶುದ್ಧ ಆಮ್ಲಜನಕ (O2) ಬಿಡುಗಡೆಯಾಗುತ್ತದೆ.",
            },
        ],
        "quiz": [
            {
                "id": "q_sci_1",
                "question_en": "Which green pigment in leaves absorbs solar energy for photosynthesis?",
                "question_kn": "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆಗೆ ಸೂರ್ಯನ ಬೆಳಕನ್ನು ಹೀರಿಕೊಳ್ಳುವ ಎಲೆಗಳ ಹಸಿರು ವರ್ಣದ್ರವ್ಯ ಯಾವುದು?",
                "options_en": ["Chlorophyll", "Hemoglobin", "Keratin", "Melanin"],
                "options_kn": ["ಕ್ಲೋರೋಫಿಲ್ (ಪತ್ರಹರಿತ್ತು)", "ಹಿಮೋಗ್ಲೋಬಿನ್", "ಕೆರಾಟಿನ್", "ಮೆಲನಿನ್"],
                "correct_index": 0,
                "explanation_en": "Chlorophyll traps sunlight inside the plant chloroplast cells.",
                "explanation_kn": "ಕ್ಲೋರೋಫಿಲ್ ಸೂರ್ಯನ ಬೆಳಕಿನ ಶಕ್ತಿಯನ್ನು ಎಲೆಗಳಲ್ಲಿ ಹೀರಿಕೊಳ್ಳುತ್ತದೆ.",
            },
            {
                "id": "q_sci_2",
                "question_en": "What life-giving gas is released by plants into the atmosphere during photosynthesis?",
                "question_kn": "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆಯ ಸಮಯದಲ್ಲಿ ಸಸ್ಯಗಳು ವಾತಾವರಣಕ್ಕೆ ಬಿಡುಗಡೆ ಮಾಡುವ ಜೀವವಾಯು ಯಾವುದು?",
                "options_en": ["Carbon Monoxide", "Oxygen", "Nitrogen", "Methane"],
                "options_kn": ["ಕಾರ್ಬನ್ ಮೊನಾಕ್ಸೈಡ್", "ಆಮ್ಲಜನಕ (Oxygen)", "ಸಾರಜನಕ", "ಮೀಥೇನ್"],
                "correct_index": 1,
                "explanation_en": "Oxygen (O2) is produced as a byproduct of water photolysis.",
                "explanation_kn": "ಸಸ್ಯಗಳು ನಮಗೆ ಉಸಿರಾಡಲು ಅಗತ್ಯವಾದ ಆಮ್ಲಜನಕವನ್ನು ಹೊರಸೂಸುತ್ತವೆ.",
            },
            {
                "id": "q_sci_3",
                "question_en": "From where do plants primarily absorb water (H2O)?",
                "question_kn": "ಸಸ್ಯಗಳು ನೀರನ್ನು ಮುಖ್ಯವಾಗಿ ಎಲ್ಲಿಂದ ಹೀರಿಕೊಳ್ಳುತ್ತವೆ?",
                "options_en": ["Flowers", "Roots from Soil", "Fruit Skin", "Bark"],
                "options_kn": ["ಹೂವುಗಳು", "ಮಣ್ಣಿನಿಂದ ಬೇರುಗಳು", "ಹಣ್ಣಿನ ಸಿಪ್ಪೆ", "ತೊಗಟೆ"],
                "correct_index": 1,
                "explanation_en": "Root hairs absorb water and minerals from moist soil through xylem vessels.",
                "explanation_kn": "ಬೇರುಗಳು ಮಣ್ಣಿನಲ್ಲಿರುವ ತೇವಾಂಶ ಮತ್ತು ನೀರನ್ನು ಜೈಲಮ್ ನಾಳಗಳ ಮೂಲಕ ಎಲೆಗಳಿಗೆ ಕಳುಹಿಸುತ್ತವೆ.",
            },
        ],
    },
    {
        "lesson_key": "kan_aksharamale_ch2",
        "pack_code": "pack_kan_aksharamale_ch2",
        "subject": "kannada",
        "grade": "Grade 5 KTBS",
        "version": 1,
        "sort_order": 3,
        "file_size_mb": 0.5,
        "package_name": "KTBS_Kannada_Aksharamale_G5.pkg.zip",
        "title_en": "Kannada Aksharamale & Swaragalu",
        "title_kn": "ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆ ಮತ್ತು ಸ್ವರಗಳು",
        "chapter_en": "Chapter 2 · Kannada",
        "chapter_kn": "ಅಧ್ಯಾಯ ೨ · ಕನ್ನಡ",
        "description_en": "Explore the phonetic harmony of Kannada vowels (ಸ್ವರಗಳು), short and long vowel distinctions, and native proverbs.",
        "description_kn": "ಹ್ರಸ್ವಸ್ವರ, ದೀರ್ಘಸ್ವರ ಮತ್ತು ಪ್ಲುತಸ್ವರಗಳ ಉಚ್ಚಾರಣಾ ನಿಯಮಗಳನ್ನು ಮತ್ತು ಪ್ರಮುಖ ಗಾದೆಗಳನ್ನು ಕಲಿಯಿರಿ.",
        "diagram_type": "aksharamale",
        "audio_text_kn": "ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆಯಲ್ಲಿ ಒಟ್ಟು ನಲವತ್ತೊಂಬತ್ತು ಅಕ್ಷರಗಳಿವೆ. ಅದರಲ್ಲಿ ಹದಿನಾಲ್ಕು ಸ್ವರಗಳು ಸೇರಿವೆ.",
        "audio_text_en": "In the Kannada alphabet there are 49 letters in total, including 14 vowels.",
        "content_sections": [
            {
                "heading_en": "1. Kannada Vowels (ಸ್ವರಗಳು)",
                "heading_kn": "೧. ಕನ್ನಡದ ಸ್ವರಗಳು",
                "body_en": "Vowels are independent sounds produced without obstruction. They include Hrasva (short sounds like ಅ, ಇ, ಉ) and Deergha (long sounds like ಆ, ಈ, ಊ).",
                "body_kn": "ಸ್ವತಂತ್ರವಾಗಿ ಉಚ್ಚರಿಸಲಾಗುವ ಅಕ್ಷರಗಳೇ ಸ್ವರಗಳು. ಇವುಗಳಲ್ಲಿ ಹ್ರಸ್ವಸ್ವರಗಳು (ಅ, ಇ, ಉ, ಋ, ಎ, ಒ) ಮತ್ತು ದೀರ್ಘಸ್ವರಗಳು (ಆ, ಈ, ಊ, ಏ, ಐ, ಓ, ಔ) ಇವೆ.",
            }
        ],
        "quiz": [
            {
                "id": "q_kan_1",
                "question_en": "How many vowels (ಸ್ವರಗಳು) are there in the standard Kannada Aksharamale?",
                "question_kn": "ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆಯಲ್ಲಿ ಒಟ್ಟು ಎಷ್ಟು ಸ್ವರಗಳಿವೆ?",
                "options_en": ["10", "12", "14", "25"],
                "options_kn": ["೧೦", "೧೨", "೧೪", "೨೫"],
                "correct_index": 2,
                "explanation_en": "There are 14 vowels in traditional Kannada script.",
                "explanation_kn": "ಸಾಂಪ್ರದಾಯಿಕ ಕನ್ನಡ ಅಕ್ಷರಮಾಲೆಯಲ್ಲಿ ೧೪ ಸ್ವರಗಳಿವೆ.",
            },
            {
                "id": "q_kan_2",
                "question_en": "Which of the following is a short vowel (ಹ್ರಸ್ವಸ್ವರ)?",
                "question_kn": "ಕೆಳಗಿನವುಗಳಲ್ಲಿ ಯಾವುದು ಹ್ರಸ್ವಸ್ವರ?",
                "options_en": ["ಅ", "ಆ", "ಈ", "ಊ"],
                "options_kn": ["ಅ", "ಆ", "ಈ", "ಊ"],
                "correct_index": 0,
                "explanation_en": "\"ಅ\" requires just one matra of time to pronounce, making it Hrasva.",
                "explanation_kn": "\"ಅ\" ಒಂದು ಮಾತ್ರಾ ಕಾಲದಲ್ಲಿ ಉಚ್ಚರಿಸಲ್ಪಡುವ ಹ್ರಸ್ವಸ್ವರವಾಗಿದೆ.",
            },
            {
                "id": "q_kan_3",
                "question_en": "Complete the proverb: \"ಕೈ ಕೆಸರಾದರೆ... (If your hand is muddied...)\"",
                "question_kn": "\"ಕೈ ಕೆಸರಾದರೆ...\" ಎಂಬ ಗಾದೆಯ ಮುಂದಿನ ಭಾಗ ಯಾವುದು?",
                "options_en": ["ಬಾಯಿ ಮೊಸರು", "ಮೈ ಕೆಸರು", "ಕಣ್ಣು ತಣ್ಣಗೆ", "ಮನಸ್ಸು ಶುದ್ಧ"],
                "options_kn": ["ಬಾಯಿ ಮೊಸರು", "ಮೈ ಕೆಸರು", "ಕಣ್ಣು ತಣ್ಣಗೆ", "ಮನಸ್ಸು ಶುದ್ಧ"],
                "correct_index": 0,
                "explanation_en": "\"ಕೈ ಕೆಸರಾದರೆ ಬಾಯಿ ಮೊಸರು\" means hard labor in the fields yields sweet nourishment.",
                "explanation_kn": "ಪ್ರಾಮಾಣಿಕ ಕಠಿಣ ದುಡಿಮೆಯಿಂದ ಸಮೃದ್ಧಿ ಲಭಿಸುತ್ತದೆ ಎಂಬುದು ಇದರ ಅರ್ಥ.",
            },
        ],
    },
    {
        "lesson_key": "soc_hoysala_ch4",
        "pack_code": "pack_soc_hoysala_ch4",
        "subject": "social",
        "grade": "Grade 7 KTBS",
        "version": 1,
        "sort_order": 4,
        "file_size_mb": 1.1,
        "package_name": "KTBS_Social_Hoysala_G7.pkg.zip",
        "title_en": "Hoysala Architecture & Heritage",
        "title_kn": "ಕರ್ನಾಟಕದ ಹೊಯ್ಸಳ ಶಿಲ್ಪಕಲೆ ಮತ್ತು ವೈಭವ",
        "chapter_en": "Chapter 4 · Social Studies",
        "chapter_kn": "ಅಧ್ಯಾಯ ೪ · ಸಮಾಜ ವಿಜ್ಞಾನ",
        "description_en": "Examine the intricate soapstone carvings, stellate (star-shaped) platforms, and lathe-turned pillars of Belur & Halebidu.",
        "description_kn": "ಬೇಲೂರು ಮತ್ತು ಹಳೇಬೀಡಿನ ನಕ್ಷತ್ರಾಕಾರದ ಜಗತಿಗಳು, ಸಾಬೂನು ಕಲ್ಲಿನ ಸೂಕ್ಷ್ಮ ಕೆತ್ತನೆಗಳು ಮತ್ತು ಲೇತ್-ಕಂಬಗಳ ಇತಿಹಾಸ ತಿಳಿಯಿರಿ.",
        "diagram_type": "water_cycle",
        "audio_text_kn": "ಹೊಯ್ಸಳ ರಾಜ ವಿಷ್ಣುವರ್ಧನನು ಕಟ್ಟಿಸಿದ ಬೇಲೂರಿನ ಚೆನ್ನಕೇಶವ ದೇವಾಲಯವು ಭಾರತೀಯ ವಾಸ್ತುಶಿಲ್ಪದ ಹೆಗ್ಗುರುತಾಗಿದೆ.",
        "audio_text_en": "The Chennakeshava Temple at Belur, built by Hoysala King Vishnuvardhana, is a crowning jewel of Indian architecture.",
        "content_sections": [
            {
                "heading_en": "1. Star-Shaped Platforms (Stellate Jagati)",
                "heading_kn": "೧. ನಕ್ಷತ್ರಾಕಾರದ ಜಗತಿ",
                "body_en": "Hoysala temples are built on raised star-shaped platforms called Jagati, providing a grand circumambulation path.",
                "body_kn": "ಹೊಯ್ಸಳ ದೇವಾಲಯಗಳನ್ನು ನಕ್ಷತ್ರಾಕಾರದ ಎತ್ತರವಾದ ಜಗತಿಯ ಮೇಲೆ ನಿರ್ಮಿಸಲಾಗಿದೆ. ಇದು ಪ್ರದಕ್ಷಿಣೆಗೆ ಅವಕಾಶ ನೀಡುತ್ತದೆ.",
            }
        ],
        "quiz": [
            {
                "id": "q_soc_1",
                "question_en": "Which soft stone was primarily carved with micro-details in Hoysala temples?",
                "question_kn": "ಹೊಯ್ಸಳ ದೇವಾಲಯಗಳ ಸೂಕ್ಷ್ಮ ಕೆತ್ತನೆಗೆ ಯಾವ ಮೃದು ಶಿಲೆಯನ್ನು ಬಳಸಲಾಗಿದೆ?",
                "options_en": ["Soapstone (Chloritic Schist)", "Sandstone", "Basalt", "Red Brick"],
                "options_kn": ["ಸಾಬೂನು ಕಲ್ಲು (ಸೋಪ್‌ಸ್ಟೋನ್)", "ಮರಳುಗಲ್ಲು", "ಬಸಾಲ್ಟ್", "ಕೆಂಪು ಇಟ್ಟಿಗೆ"],
                "correct_index": 0,
                "explanation_en": "Soapstone is soft when quarried and hardens upon exposure to air.",
                "explanation_kn": "ಸಾಬೂನು ಕಲ್ಲು ಆರಂಭದಲ್ಲಿ ಮೃದುವಾಗಿದ್ದು, ನಂತರ ಗಟ್ಟಿಯಾಗುತ್ತದೆ.",
            },
            {
                "id": "q_soc_2",
                "question_en": "In which Karnataka district is the world-famous Belur Chennakeshava Temple located?",
                "question_kn": "ಪ್ರಸಿದ್ಧ ಬೇಲೂರು ಚೆನ್ನಕೇಶವ ದೇವಾಲಯವು ಕರ್ನಾಟಕದ ಯಾವ ಜಿಲ್ಲೆಯಲ್ಲಿದೆ?",
                "options_en": ["Hassan", "Mysuru", "Ballari", "Belagavi"],
                "options_kn": ["ಹಾಸನ", "ಮೈಸೂರು", "ಬಳ್ಳಾರಿ", "ಬೆಳಗಾವಿ"],
                "correct_index": 0,
                "explanation_en": "Belur is in Hassan district of Karnataka.",
                "explanation_kn": "ಬೇಲೂರು ಕರ್ನಾಟಕದ ಹಾಸನ ಜಿಲ್ಲೆಯಲ್ಲಿದೆ.",
            },
            {
                "id": "q_soc_3",
                "question_en": "What unique platform shape is characteristic of Hoysala temples?",
                "question_kn": "ಹೊಯ್ಸಳ ದೇವಾಲಯಗಳ ಅಡಿಪಾಯದ (ಜಗತಿ) ವಿಶೇಷ ಆಕಾರ ಯಾವುದು?",
                "options_en": ["Star-shaped (Stellate)", "Hexagonal", "Octagonal", "Circular"],
                "options_kn": ["ನಕ್ಷತ್ರಾಕಾರ", "ಷಡ್ಭುಜಾಕಾರ", "ಅಷ್ಟಭುಜಾಕಾರ", "ವೃತ್ತಾಕಾರ"],
                "correct_index": 0,
                "explanation_en": "The stellate star shape maximized exterior surface area for sculptures.",
                "explanation_kn": "ನಕ್ಷತ್ರಾಕಾರದ ರಚನೆಯು ಗೋಡೆಗಳ ಮೇಲೆ ಹೆಚ್ಚಿನ ಶಿಲ್ಪಗಳನ್ನು ಕೆತ್ತಲು ನೆರವಾಗುತ್ತದೆ.",
            },
        ],
    },
]

BADGES_SEED = [
    {
        "badge_key": "First Lesson",
        "name_en": "First Step Scholar",
        "name_kn": "ಮೊದಲ ಹೆಜ್ಜೆ ವಿದ್ವಾಂಸ",
        "description_en": "Completed your very first offline curriculum module.",
        "description_kn": "ನಿಮ್ಮ ಮೊದಲ ಆಫ್‌ಲೈನ್ ಪಠ್ಯಕ್ರಮ ಮಾಡ್ಯೂಲ್ ಪೂರ್ಣಗೊಳಿಸಿದ್ದೀರಿ.",
        "icon": "BookOpen",
        "rarity": "bronze",
        "sort_order": 1,
    },
    {
        "badge_key": "3-Day Streak",
        "name_en": "3-Day Flame",
        "name_kn": "೩ ದಿನಗಳ ನಿರಂತರ ಅಧ್ಯಯನ",
        "description_en": "Maintained a 3-day continuous offline study streak.",
        "description_kn": "ಸತತ ೩ ದಿನಗಳ ಕಾಲ ನಿರಂತರವಾಗಿ ಕಲಿಕೆ ಮುಂದುವರಿಸಿದ್ದೀರಿ.",
        "icon": "Flame",
        "rarity": "bronze",
        "sort_order": 2,
    },
    {
        "badge_key": "P2P Sharer",
        "name_en": "P2P Mesh Sharer",
        "name_kn": "P2P ಮೆಶ್ ಹಂಚಿಕೆದಾರ",
        "description_en": "Shared a curriculum package with a peer phone via Wi-Fi Direct with zero data.",
        "description_kn": "ಇಂಟರ್ನೆಟ್ ಇಲ್ಲದೆ ವೈ-ಫೈ ಡೈರೆಕ್ಟ್ ಮೂಲಕ ಸಹಪಾಠಿಗೆ ಪಾಠ ಹಂಚಿದ್ದೀರಿ.",
        "icon": "Radio",
        "rarity": "silver",
        "sort_order": 3,
    },
    {
        "badge_key": "P2P Pioneer",
        "name_en": "Village Mesh Pioneer",
        "name_kn": "ಗ್ರಾಮ ಮೆಶ್ ಪ್ರವರ್ತಕ",
        "description_en": "Distributed 3 or more modules to classmates in the village network.",
        "description_kn": "ಗ್ರಾಮದ ಜಾಲದಲ್ಲಿ ೩ ಕ್ಕೂ ಹೆಚ್ಚು ಪಾಠಗಳನ್ನು ಯಶಸ್ವಿಯಾಗಿ ವಿತರಿಸಿದ್ದೀರಿ.",
        "icon": "Share2",
        "rarity": "gold",
        "sort_order": 4,
    },
    {
        "badge_key": "Math Master",
        "name_en": "Ganitha Visharada",
        "name_kn": "ಗಣಿತ ವಿಶಾರದ",
        "description_en": "Scored 100% on the KTBS Fractions knowledge check quiz.",
        "description_kn": "ಅಪೂರ್ಣಾಂಕಗಳ ರಸಪ್ರಶ್ನೆಯಲ್ಲಿ ಪೂರ್ಣ ಅಂಕ ಗಳಿಸಿದ್ದೀರಿ.",
        "icon": "Divide",
        "rarity": "gold",
        "sort_order": 5,
    },
    {
        "badge_key": "7-Day Flame",
        "name_en": "7-Day Iron Streak",
        "name_kn": "೭ ದಿನಗಳ ಅಗ್ನಿ ಸ್ಟ್ರೀಕ್",
        "description_en": "Kept the learning fire burning strong for a full week without breaking.",
        "description_kn": "ಪೂರ್ಣ ಒಂದು ವಾರ ಯಾವುದೇ ದಿನ ತಪ್ಪದೇ ಅಧ್ಯಯನ ನಡೆಸಿದ್ದೀರಿ.",
        "icon": "Zap",
        "rarity": "gold",
        "sort_order": 6,
    },
    {
        "badge_key": "Quiz Wizard",
        "name_en": "Prashna Ratna",
        "name_kn": "ಪ್ರಶ್ನ ರತ್ನ",
        "description_en": "Answered over 10 quiz questions correctly on the first attempt.",
        "description_kn": "ಹತ್ತಕ್ಕೂ ಹೆಚ್ಚು ರಸಪ್ರಶ್ನೆಗಳಿಗೆ ಮೊದಲ ಪ್ರಯತ್ನದಲ್ಲೇ ಸರಿಯುತ್ತರ ನೀಡಿದ್ದೀರಿ.",
        "icon": "Award",
        "rarity": "platinum",
        "sort_order": 7,
    },
]

QUESTS_SEED = [
    {
        "quest_key": "quest_science",
        "title_en": "Complete 1 Science Module",
        "title_kn": "೧ ವಿಜ್ಞಾನ ಮಾಡ್ಯೂಲ್ ಪೂರ್ಣಗೊಳಿಸಿ",
        "sub_en": "Explore photosynthesis or water cycle",
        "sub_kn": "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆ ಅಥವಾ ಜಲಚಕ್ರ ಓದಿ",
        "xp_reward": 50,
        "target_type": "complete_science",
        "max_progress": 1,
        "sort_order": 1,
    },
    {
        "quest_key": "quest_quiz_100",
        "title_en": "Score 100% on a Quiz",
        "title_kn": "ರಸಪ್ರಶ್ನೆಯಲ್ಲಿ ೧೦೦% ಅಂಕ ಗಳಿಸಿ",
        "sub_en": "Answer all 3 questions correctly in any subject",
        "sub_kn": "ಯಾವುದೇ ವಿಷಯದಲ್ಲಿ ಎಲ್ಲಾ ೩ ಪ್ರಶ್ನೆಗಳಿಗೆ ಸರಿಯುತ್ತರ ನೀಡಿ",
        "xp_reward": 70,
        "target_type": "quiz_100",
        "max_progress": 1,
        "sort_order": 2,
    },
    {
        "quest_key": "quest_p2p_share",
        "title_en": "Simulate P2P Module Share",
        "title_kn": "P2P ಮಾಡ್ಯೂಲ್ ಹಂಚಿಕೆಯನ್ನು ಪೂರ್ಣಗೊಳಿಸಿ",
        "sub_en": "Transmit 1 module over Wi-Fi Direct mesh",
        "sub_kn": "ವೈ-ಫೈ ಡೈರೆಕ್ಟ್ ಮೂಲಕ ೧ ಪಾಠವನ್ನು ವರ್ಗಾಯಿಸಿ",
        "xp_reward": 100,
        "target_type": "p2p_share",
        "max_progress": 1,
        "sort_order": 3,
    },
]

LEADERBOARD_PEERS_SEED = [
    {
        "peer_key": "lead_1",
        "name": "Ananya Rao",
        "village": "Channapatna",
        "school": "Govt Model Primary School",
        "total_xp": 850,
        "streak": 9,
    },
    {
        "peer_key": "lead_3",
        "name": "Raju Kumar",
        "village": "Ramanagara",
        "school": "Rural Composite PU College",
        "total_xp": 320,
        "streak": 4,
    },
    {
        "peer_key": "lead_4",
        "name": "Sneha C",
        "village": "Kanakapura",
        "school": "Govt Girls High School",
        "total_xp": 290,
        "streak": 3,
    },
    {
        "peer_key": "lead_5",
        "name": "Basavaraj Patil",
        "village": "Magadi",
        "school": "Taluk Model High School",
        "total_xp": 210,
        "streak": 2,
    },
    {
        "peer_key": "lead_6",
        "name": "Kavya Gowda",
        "village": "Maddur",
        "school": "Sri Chennakeshava Vidyalaya",
        "total_xp": 180,
        "streak": 1,
    },
]


def _build_pack_archive(lesson_data: dict, packs_dir: Path) -> tuple[str, str]:
    archive_path = packs_dir / lesson_data["package_name"]
    manifest = {
        "pack_code": lesson_data["pack_code"],
        "package_name": lesson_data["package_name"],
        "version": lesson_data["version"],
        "subject": lesson_data["subject"],
        "grade": lesson_data["grade"],
        "lesson_key": lesson_data["lesson_key"],
    }
    lesson_payload = {
        "lesson_key": lesson_data["lesson_key"],
        "subject": lesson_data["subject"],
        "grade": lesson_data["grade"],
        "title_en": lesson_data["title_en"],
        "title_kn": lesson_data["title_kn"],
        "chapter_en": lesson_data["chapter_en"],
        "chapter_kn": lesson_data["chapter_kn"],
        "description_en": lesson_data["description_en"],
        "description_kn": lesson_data["description_kn"],
        "diagram_type": lesson_data["diagram_type"],
        "audio_text_en": lesson_data["audio_text_en"],
        "audio_text_kn": lesson_data["audio_text_kn"],
        "content_sections": lesson_data["content_sections"],
        "quiz": lesson_data["quiz"],
    }
    with zipfile.ZipFile(archive_path, "w", compression=zipfile.ZIP_DEFLATED) as zf:
        zf.writestr("manifest.json", json.dumps(manifest, ensure_ascii=False, indent=2))
        zf.writestr("lesson.json", json.dumps(lesson_payload, ensure_ascii=False, indent=2))

    digest = hashlib.sha256(archive_path.read_bytes()).hexdigest()
    return str(archive_path), digest


def seed_initial_data(db: Session, packs_dir: Path | None = None) -> None:
    target_packs_dir = packs_dir or settings.packs_path
    target_packs_dir.mkdir(parents=True, exist_ok=True)

    for item in LESSONS_SEED:
        archive_path, sha256_hex = _build_pack_archive(item, target_packs_dir)
        pack = db.scalar(select(ContentPack).where(ContentPack.pack_code == item["pack_code"]))
        if pack is None:
            pack = ContentPack(
                pack_code=item["pack_code"],
                subject=item["subject"],
                grade=item["grade"],
                version=item["version"],
                package_name=item["package_name"],
                file_size_mb=item["file_size_mb"],
                sha256=sha256_hex,
                archive_path=archive_path,
            )
            db.add(pack)
            db.flush()
        else:
            pack.sha256 = sha256_hex
            pack.archive_path = archive_path

        lesson = db.scalar(select(LessonModule).where(LessonModule.lesson_key == item["lesson_key"]))
        if lesson is None:
            lesson = LessonModule(
                pack_id=pack.id,
                lesson_key=item["lesson_key"],
                subject=item["subject"],
                grade=item["grade"],
                sort_order=item["sort_order"],
                title_en=item["title_en"],
                title_kn=item["title_kn"],
                chapter_en=item["chapter_en"],
                chapter_kn=item["chapter_kn"],
                description_en=item["description_en"],
                description_kn=item["description_kn"],
                diagram_type=item["diagram_type"],
                audio_text_en=item["audio_text_en"],
                audio_text_kn=item["audio_text_kn"],
                sections_json=json.dumps(item["content_sections"], ensure_ascii=False),
                quiz_json=json.dumps(item["quiz"], ensure_ascii=False),
            )
            db.add(lesson)

    for b in BADGES_SEED:
        if db.scalar(select(Badge).where(Badge.badge_key == b["badge_key"])) is None:
            db.add(Badge(**b))

    for q in QUESTS_SEED:
        if db.scalar(select(DailyQuest).where(DailyQuest.quest_key == q["quest_key"])) is None:
            db.add(DailyQuest(**q))

    for p in LEADERBOARD_PEERS_SEED:
        if db.scalar(select(LeaderboardPeer).where(LeaderboardPeer.peer_key == p["peer_key"])) is None:
            db.add(LeaderboardPeer(**p))

    db.commit()
