-- 구매자 정보 일괄 등록 SQL
-- H2 Database용

INSERT INTO customers (customer_name, email, phone, address, payment_completed, payment_method, notes, created_at, updated_at) VALUES
('부기', 'bugi@example.com', '010-1234-5678', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('나연주', 'nayeonju@example.com', '010-2345-6789', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('이수아', 'leesuah@example.com', '010-3456-7890', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('윤땅띵똥', 'yoontang@example.com', '010-4567-8901', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('두부', 'tofu@example.com', '010-5678-9012', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('콩아현', 'kongahyeon@example.com', '010-6789-0123', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('최은지', 'choieunji@example.com', '010-7890-1234', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('안진희', 'anjinhee@example.com', '010-8901-2345', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('손가희', 'songahee@example.com', '010-9012-3456', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('이여진', 'leeyeojin@example.com', '010-0123-4567', '', false, null, '구매자 등록 (이영임과 동일인)', CURRENT_DATE, CURRENT_DATE),
('이소망', 'leesomang@example.com', '010-1357-2468', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('박혜경', 'parkhyekyung@example.com', '010-2468-1357', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('홍민재', 'hongminjae@example.com', '010-3691-4702', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('이효원', 'leehyowon@example.com', '010-4815-6273', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('혠짱', 'hyunjjang@example.com', '010-5926-3704', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE),
('변소윤', 'byeonsoyoon@example.com', '010-6037-8159', '', false, null, '구매자 등록', CURRENT_DATE, CURRENT_DATE);

-- 등록 확인 쿼리
SELECT id, customer_name, email, phone, payment_completed, created_at 
FROM customers 
ORDER BY created_at DESC;