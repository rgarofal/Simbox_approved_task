CREATE TABLE IF NOT EXISTS simbox_timestamp_idx (
    id int(11) NOT NULL AUTO_INCREMENT,
    date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    folder varchar(255) NOT NULL,
    filename varchar(255) NOT NULL,
    dl varchar(255) NOT NULL,
PRIMARY KEY (id)
) ENGINE=MyISAM AUTO_INCREMENT=11919 DEFAULT CHARSET=latin1;