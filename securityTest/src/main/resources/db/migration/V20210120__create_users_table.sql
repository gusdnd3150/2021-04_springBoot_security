CREATE TABLE USERS(
	id INT NOT NULL auto_increment,
	email varchar (100) NOT NULL,
	userId varchar (25) NOT NULL,
	userName varchar (25) NOT NULL,
	password varchar (200) NOT NULL,
	autho varchar (200) NOT NULL,
     isAccountNonExpired boolean,
     isAccountNonLocked boolean,
     isCredentialsNonExpired boolean,
     isEnabled boolean,
	
	primary key (id)
)