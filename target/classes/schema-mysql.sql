DROP TABLE IF EXISTS people;

CREATE TABLE people  (
    person_id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    primary key (person_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS simbox_timestamp_idx (
    id int(11) NOT NULL AUTO_INCREMENT,
    date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    folder varchar(255) NOT NULL,
    filename varchar(255) NOT NULL,
    dl varchar(255) NOT NULL,
PRIMARY KEY (id)
) ENGINE=MyISAM AUTO_INCREMENT=11919 DEFAULT CHARSET=latin1;