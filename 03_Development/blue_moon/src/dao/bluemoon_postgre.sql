--- Tạo database ---
CREATE DATABASE bluemoon
    WITH
    OWNER = postgres
    TEMPLATE = template0
    ENCODING = 'UTF8'
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

--- Bảng member ---
CREATE TABLE IF NOT EXISTS member (
    id VARCHAR PRIMARY KEY,
    household_id INTEGER,
    full_name VARCHAR NOT NULL,
    gender VARCHAR NOT NULL,
    date_of_birth DATE,
    id_card VARCHAR UNIQUE,
    relationship VARCHAR NOT NULL,
    occupation VARCHAR,
    is_household_head BOOLEAN DEFAULT FALSE
);

--- Insert sample data for member table ---
INSERT INTO member (id, household_id, full_name, gender, date_of_birth, id_card, relationship, occupation, is_household_head)
VALUES
('M001', 1, 'Nguyen Van A', 'MALE', '1975-04-12', '123456789', 'FATHER', 'Engineer', true),
('M002', 1, 'Nguyen Thi A1', 'FEMALE', '1978-09-20', '123456790', 'MOTHER', 'Teacher', false),
('M003', 1, 'Nguyen Van A2', 'MALE', '2005-06-01', '123456791', 'SON', 'Student', false),
('M004', 2, 'Tran Thi B', 'FEMALE', '1980-01-01', '223456789', 'MOTHER', 'Nurse', true),
('M005', 2, 'Le Van B1', 'MALE', '1975-03-11', '223456790', 'FATHER', 'Driver', false),
('M006', 2, 'Le Thi B2', 'FEMALE', '2006-10-09', '223456791', 'DAUGHTER', 'Student', false),
('M007', 2, 'Le Van B3', 'MALE', '2008-07-07', '223456792', 'SON', 'Student', false),
('M008', 2, 'Le Van B4', 'MALE', '2010-01-25', '223456793', 'SON', 'Student', false),
('M009', 3, 'Le Van C', 'MALE', '1970-12-12', '323456789', 'FATHER', 'Retired', true),
('M010', 3, 'Pham Thi C1', 'FEMALE', '1972-05-10', '323456790', 'MOTHER', 'Housewife', false),
('M011', 3, 'Le Van C2', 'MALE', '2003-08-15', '323456791', 'SON', 'Student', false),
('M012', 4, 'Pham Thi D', 'FEMALE', '1985-02-28', '423456789', 'MOTHER', 'Engineer', true),
('M013', 4, 'Nguyen Van D1', 'MALE', '1982-04-30', '423456790', 'FATHER', 'Police', false),
('M014', 4, 'Nguyen Thi D2', 'FEMALE', '2005-09-09', '423456791', 'DAUGHTER', 'Student', false),
('M015', 4, 'Nguyen Van D3', 'MALE', '2007-06-25', '423456792', 'SON', 'Student', false),
('M016', 4, 'Nguyen Thi D4', 'FEMALE', '2009-11-30', '423456793', 'DAUGHTER', 'Student', false),
('M017', 4, 'Nguyen Van D5', 'MALE', '2011-01-05', '423456794', 'SON', 'Student', false);

--- Bảng users ---
CREATE TABLE IF NOT EXISTS users (
    login_name VARCHAR PRIMARY KEY,
    user_name VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    role VARCHAR CHECK (role IN ('ADMIN', 'ACCOUNTANT', 'LEADER'))
);

--- Bảng households ---
CREATE TABLE IF NOT EXISTS households (
    household_id SERIAL PRIMARY KEY,
    house_number VARCHAR CHECK (
        house_number ~ '^Nhà_[0-9]+/Tầng_[0-9]+/BlueMoon$'
    ),
    district VARCHAR,
    ward VARCHAR,
    street VARCHAR,
    registration_date DATE,
    number_of_residents INTEGER,
    head_resident_id INTEGER
);

--- Bảng residents ---
CREATE TABLE IF NOT EXISTS residents (
    resident_id SERIAL PRIMARY KEY,
    full_name VARCHAR,
    date_of_birth DATE,
    gender VARCHAR,
    ethnicity VARCHAR,
    religion VARCHAR,
    citizen_id VARCHAR UNIQUE,
    date_of_issue DATE,
    place_of_issue VARCHAR,
    occupation VARCHAR,
    notes TEXT,
    added_date DATE,
    relationship_with_head VARCHAR,
    household_id INTEGER REFERENCES households(household_id)
);

--- Thêm khóa ngoài (mã chủ hộ) cho bảng households ---
ALTER TABLE households
ADD CONSTRAINT fk_head_resident
FOREIGN KEY (head_resident_id) REFERENCES residents(resident_id);

--- Bảng stay_absence_records ---
CREATE TABLE IF NOT EXISTS stay_absence_records (
    record_id SERIAL PRIMARY KEY,
    record_type VARCHAR,
    created_date DATE,
    temp_address VARCHAR,
    period VARCHAR,
    request_desc TEXT,
    resident_id INTEGER REFERENCES residents(resident_id)
);

--- Bảng change_history_records ---
CREATE TABLE IF NOT EXISTS change_history_records (
    record_id SERIAL PRIMARY KEY,
    change_type VARCHAR,
    change_date DATE,
    resident_id INTEGER REFERENCES residents(resident_id),
    household_id INTEGER REFERENCES households(household_id)
);

--- Bảng fees ---
CREATE TABLE fees (
    fee_id SERIAL PRIMARY KEY,
    name VARCHAR,
    created_date DATE,
    is_mandatory BOOLEAN,
    description TEXT
);


--- Bảng fee_payment_records ---
CREATE TABLE fee_payment_records (
    payment_id SERIAL PRIMARY KEY,
    expected_amount NUMERIC,
    paid_amount NUMERIC,
    paid_date DATE,
    is_fully_paid BOOLEAN,
    household_id INTEGER REFERENCES households(household_id),
    fee_id INTEGER REFERENCES fees(fee_id),
    campaign_fee_id INTEGER REFERENCES campaign_fees(campaign_fee_id)
);


--- Bảng campaign_fees ---
CREATE TABLE campaign_fees (
    campaign_fee_id SERIAL PRIMARY KEY,
    name VARCHAR,
    created_date DATE,
    start_date DATE,
    due_date DATE,
    status VARCHAR,
    description TEXT
);

--- Bảng campaign_fee_items ---
CREATE TABLE campaign_fee_items (
    fee_id INTEGER REFERENCES fees(fee_id),
    campaign_fee_id INTEGER REFERENCES campaign_fees(campaign_fee_id),
    PRIMARY KEY (fee_id, campaign_fee_id)
);


--- Chèn dữ liệu mẫu cho bảng households ---
INSERT INTO households (house_number, district, ward, street, registration_date)
VALUES
('Nhà_6/Tầng_3/BlueMoon', 'Hà Đông', 'Phú La', 'Hà Nội', '2022-01-01'),
('Nhà_8/Tầng_2/BlueMoon', 'Hà Đông', 'Phú La', 'Hà Nội', '2023-03-15');


--- Chèn dữ liệu mẫu cho bảng residents ---
INSERT INTO residents (
    full_name, date_of_birth, gender, ethnicity, religion, citizen_id,
    date_of_issue, place_of_issue, occupation, notes, added_date, relationship_with_head, household_id
)
VALUES
('Nguyễn Văn An', '1980-05-20', 'Nam', 'Kinh', 'Không', '012345678901',
 '2010-06-01', 'Hà Nội', 'Công nhân', NULL, '2022-01-02', 'Chủ hộ', 1),
('Trần Thị Bích', '1985-09-10', 'Nữ', 'Kinh', 'Phật giáo', '012345678902',
 '2011-08-20', 'Hà Nội', 'Giáo viên', NULL, '2022-01-02', 'Vợ', 1),
('Ngô Văn Anh', '1981-05-20', 'Nam', 'Kinh', 'Không', '012945678901',
 '2010-06-01', 'Hà Nội', 'Công nhân', NULL, '2022-09-02', 'Chủ hộ', 2),
('Trần Thị Thanh Hà', '1982-09-10', 'Nữ', 'Kinh', 'Phật giáo', '032345678902',
 '2011-08-20', 'Hà Nội', 'Giáo viên', NULL, '2022-09-02', 'Vợ', 2),
('Ngô Văn Cường', '2010-03-18', 'Nam', 'Kinh', 'Không', '012345678903',
 '2014-02-14', 'Hà Nội', 'Học sinh', NULL, '2022-09-02', 'Con', 2);


--- Sửa lại trường khóa ngoài (mã chủ hộ) của bảng house_holds ---
UPDATE households
SET head_resident_id = 1
WHERE household_id = 1;

UPDATE households
SET head_resident_id = 3
WHERE household_id = 2;


--- Chèn dữ liệu mẫu cho bảng fees ---
INSERT INTO fees (name, created_date, is_mandatory, description)
VALUES 
('Phí vệ sinh', '2025-05-01', TRUE, 'Thu tiền vệ sinh hàng tháng'),
('Phí gửi xe', '2025-05-01', TRUE, 'Thu tiền gửi xe hàng tháng'),
('Tiền điện', '2025-05-01', TRUE, 'Thu tiền điện hàng tháng'),
('Tiền nước', '2025-05-01', TRUE, 'Thu tiền nước hàng tháng'),
('Tổ chức Trung Thu', '2025-05-05', FALSE, 'Thu tiền tổ chức trung thu'),
('Tổ chức 1/6', '2025-05-20', FALSE, 'Thu tiền tổ chức 1/6'),
('Ủng hộ người nghèo khó', '2025-05-20', FALSE, 'Ủng hộ quỹ vì người nghèo');



--- Chèn dữ liệu mẫu cho bảng campaign_fees ---
INSERT INTO campaign_fees (name, created_date, start_date, due_date, status, description)
VALUES 
('Đợt thu phí tháng 4/2025', '2025-04-01', '2025-04-10', '2025-04-29', 'Đã kết thúc', 'Thu phí toàn thể các hộ dân trong chung cư tháng 4/2025'),
('Đợt thu phí tháng 5/2025', '2025-05-05', '2025-05-10', '2025-05-31', 'Đang diễn ra', 'Thu phí toàn thể các hộ dân trong chung cư tháng 5/2025'),
('Chiến dịch Hỗ trợ người nghèo', '2025-05-02', '2025-05-06', '2025-05-31', 'Đang diễn ra', 'Quyên góp hỗ trợ các hộ khó khăn trong khu vực');


-- Chèn dữ liệu mẫu cho bảng campaign_fee_items
INSERT INTO campaign_fee_items (fee_id, campaign_fee_id)
VALUES 
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(1, 2),
(2, 2),
(3, 2),
(4, 2),
(6, 2),
(7, 3);


--- Chèn dữ liệu mẫu cho bảng users ---
INSERT INTO users (login_name, role, user_name, password)
VALUES 
('haivietb9@gmail.com', 'ACCOUNTANT', 'Đinh Đình Hải Việt', 'd043a5ae298b885f7448ae9f7849743c5c9f88fee38bb3c1a3f07d9ad9aa479d'),
('doannhatquang0@gmail.com', 'ACCOUNTANT', 'Đoàn Nhật Quang', '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824'),
('Dinh1duc2anh3@gmail.com', 'LEADER', 'Đinh Đức Anh', '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824'),
('1', 'LEADER', 'Đinh Đức Anh', '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824'),
('buituanphong1501@gmail.com', 'LEADER', 'Bùi Tuấn Phong', '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824'),
('daiduong2004bn@gmail.com', 'LEADER', 'Nguyễn Đức Đại Dương', '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824');