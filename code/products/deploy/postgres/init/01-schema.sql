CREATE DATABASE gema;

-- public.t_town definition

\c gema;
-- Drop table

-- DROP TABLE public.t_town;

CREATE TABLE public.t_town (
	id int8 NOT NULL,
	code int4 NULL,
	"location" public.geometry(point, 4326) NULL,
	"name" varchar(255) NULL,
	population float8 NULL,
	population_date date NULL,
	CONSTRAINT t_town_pkey PRIMARY KEY (id)
);


-- public.t_municipality definition

-- Drop table

-- DROP TABLE public.t_municipality;

CREATE TABLE public.t_municipality (
	id int8 NOT NULL,
	area float8 NULL,
	"extension" public.geometry(multipolygon, 4326) NULL,
	ine_code varchar(255) NULL,
	"name" varchar(255) NULL,
	population int4 NULL,
	capital int8 NULL,
	CONSTRAINT t_municipality_pkey PRIMARY KEY (id),
	CONSTRAINT fkje632yh994320jxh2rv1puhbw FOREIGN KEY (capital) REFERENCES public.t_town(id)
);


-- public.t_province definition

-- Drop table

-- DROP TABLE public.t_province;

CREATE TABLE public.t_province (
	id int8 NOT NULL,
	area float8 NULL,
	"extension" public.geometry(multipolygon, 4326) NULL,
	"name" varchar(255) NULL,
	population float8 NULL,
	capital int8 NULL,
	CONSTRAINT t_province_pkey PRIMARY KEY (id),
	CONSTRAINT fksyxgc3f4xdxmhp8lgepu3mlsi FOREIGN KEY (capital) REFERENCES public.t_town(id)
);


-- public.t_region definition

-- Drop table

-- DROP TABLE public.t_region;

CREATE TABLE public.t_region (
	id int8 NOT NULL,
	area float8 NULL,
	"extension" public.geometry(multipolygon, 4326) NULL,
	"name" varchar(255) NULL,
	population float8 NULL,
	capital int8 NULL,
	CONSTRAINT t_region_pkey PRIMARY KEY (id),
	CONSTRAINT fkk36t89geap0ydwwb9nshxdrvu FOREIGN KEY (capital) REFERENCES public.t_town(id)
);