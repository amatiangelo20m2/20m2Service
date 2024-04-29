select * FROM branch;

select * from booking;

select * from user_entity;

select * from branch_user;

select * from supplier;

select * from branch_supplier;

select * from order_entity;

delete from order_items where order_id = 48;
delete from order_entity where branch_id = 3;

delete from branch_user where authorized = false;
delete from branch_user where id = 14;
delete from branch_user where role != 0;
delete from branch_user where id < 7;

truncate table notification_entity;

update branch_user set authorized = true where role = 1;
update branch_user set role = 0 where role = 1;


truncate table order_items cascade;
truncate table order_entity cascade;

select * from public.notification_entity where public.notification_entity.fmc_token
                                            = 'eObSBEUBuUpjtfuCZ_o0Db:APA91bEDdrz0VfPoiP7wUCmOT7WxooHu2QGpSN8njza5vwF_T_qLvgTbNgZdMt_mcjEzbx605PW26hrtnofJ5svj_eUN6DMa6hcq0O7BJFGh5E7REMTO0Ab-q2FcfFs5xubZfBnbkQOI'
                                    AND public.notification_entity.insertion_date >= '2024-04-20' ORDER BY notification_id DESC;