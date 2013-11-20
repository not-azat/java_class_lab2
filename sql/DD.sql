create database spatial_lab;

drop table notes;
DROP table locations;

CREATE TABLE locations (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    location GEOMETRY NOT NULL,
	primary key (id),
	SPATIAL KEY (location)
)  ENGINE=MyISAM CHARSET=UTF8;

CREATE TABLE notes (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	text TEXT NOT NULL,
	locationId INT NOT NULL,
	FOREIGN KEY (locationId)
		REFERENCES locations(id)
		ON DELETE CASCADE
) ENGINE=MyISAM CHARSET=UTF8;