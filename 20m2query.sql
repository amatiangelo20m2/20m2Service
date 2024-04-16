select * FROM branch;

select * from booking;

select * from user_entity;

select * from branch_user;

select * from supplier;

select * from branch_supplier;

select * from order_entity;

delete
from branch_user
where authorized = false;
delete
from branch_user
where authorized = false;