
-- Fuel Calculator Localization - Database Schema
-- Run this file once in MySQL/MariaDB before starting the app

CREATE DATABASE IF NOT EXISTS fuel_calculator_localization
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fuel_calculator_localization;


-- Table 1: Saves every calculation the user performs
CREATE TABLE IF NOT EXISTS calculation_records (
                                                   id          INT AUTO_INCREMENT PRIMARY KEY,
                                                   distance    DOUBLE       NOT NULL,
                                                   consumption DOUBLE       NOT NULL,
                                                   price       DOUBLE       NOT NULL,
                                                   total_fuel  DOUBLE       NOT NULL,
                                                   total_cost  DOUBLE       NOT NULL,
                                                   language    VARCHAR(10),
                                                   created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: Stores all UI text per language (replaces .properties files)
CREATE TABLE IF NOT EXISTS localization_strings (
                                                    id       INT AUTO_INCREMENT PRIMARY KEY,
                                                    `key`    VARCHAR(100) NOT NULL,
                                                    value    VARCHAR(255) NOT NULL,
                                                    language VARCHAR(10)  NOT NULL,
                                                    UNIQUE KEY unique_key_lang (`key`, `language`)
);


-- Seed Data: Insert all UI strings for all 4 languages


-- English (EN)
INSERT INTO localization_strings (`key`, value, language) VALUES
                                                              ('title',               'Fuel Consumption Calculator',              'en'),
                                                              ('distance',            'Distance (km):',                           'en'),
                                                              ('fuel',                'Fuel consumption (L/100km):',              'en'),
                                                              ('price',               'Fuel price (€/L):',                       'en'),
                                                              ('calculate',           'Calculate',                                'en'),
                                                              ('fuel_result',         'Fuel needed: %.2f L   Total cost: %.2f €', 'en'),
                                                              ('error_invalid_input', 'Please enter valid positive numbers.',      'en'),
                                                              ('enter_distance',      'Enter distance in km',                     'en'),
                                                              ('enter_fuel',          'Enter consumption rate',                   'en'),
                                                              ('enter_price',         'Enter fuel price',                         'en');

-- French (FR)
INSERT INTO localization_strings (`key`, value, language) VALUES
                                                              ('title',               'Calculateur de carburant',                       'fr'),
                                                              ('distance',            'Distance (km) :',                                'fr'),
                                                              ('fuel',                'Consommation (L/100km) :',                       'fr'),
                                                              ('price',               'Prix du carburant (€/L) :',                     'fr'),
                                                              ('calculate',           'Calculer',                                       'fr'),
                                                              ('fuel_result',         'Carburant : %.2f L   Coût total : %.2f €',       'fr'),
                                                              ('error_invalid_input', 'Veuillez entrer des nombres positifs valides.',   'fr'),
                                                              ('enter_distance',      'Entrez la distance en km',                       'fr'),
                                                              ('enter_fuel',          'Entrez le taux de consommation',                 'fr'),
                                                              ('enter_price',         'Entrez le prix du carburant',                    'fr');

-- Japanese (JA)
INSERT INTO localization_strings (`key`, value, language) VALUES
                                                              ('title',               '燃料消費計算機',                             'ja'),
                                                              ('distance',            '距離（km）：',                               'ja'),
                                                              ('fuel',                '燃費（L/100km）：',                          'ja'),
                                                              ('price',               '燃料価格（€/L）：',                          'ja'),
                                                              ('calculate',           '計算する',                                   'ja'),
                                                              ('fuel_result',         '必要燃料：%.2f L   合計費用：%.2f €',         'ja'),
                                                              ('error_invalid_input', '有効な正の数を入力してください。',              'ja'),
                                                              ('enter_distance',      '距離を入力（km）',                           'ja'),
                                                              ('enter_fuel',          '燃費を入力',                                 'ja'),
                                                              ('enter_price',         '燃料価格を入力',                             'ja');

-- Persian / Farsi (FA)
INSERT INTO localization_strings (`key`, value, language) VALUES
                                                              ('title',               'ماشین‌حساب مصرف سوخت',                     'fa'),
                                                              ('distance',            'مسافت (کیلومتر):',                          'fa'),
                                                              ('fuel',                'مصرف سوخت (L/100km):',                      'fa'),
                                                              ('price',               'قیمت سوخت (€/L):',                          'fa'),
                                                              ('calculate',           'محاسبه',                                    'fa'),
                                                              ('fuel_result',         'سوخت مورد نیاز: %.2f L   هزینه کل: %.2f €', 'fa'),
                                                              ('error_invalid_input', 'لطفاً اعداد مثبت معتبر وارد کنید.',          'fa'),
                                                              ('enter_distance',      'مسافت را وارد کنید',                        'fa'),
                                                              ('enter_fuel',          'نرخ مصرف را وارد کنید',                     'fa'),
                                                              ('enter_price',         'قیمت سوخت را وارد کنید',                    'fa');