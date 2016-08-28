CREATE TABLE reverie_meta_data (
       id serial primary key,
       name text not null default '',
       data text not null default '',
       type text not null default '',
       active_p boolean not null default false
);


CREATE TABLE reverie_meta_set (
       id serial primary key,
       name text not null default '',
       all_pages_p boolean not null default false
);

CREATE TABLE reverie_meta_set_meta_data (
       meta_data_id integer references reverie_meta_data(id),
       meta_set_id integer references reverie_meta_set(id)
);

CREATE TABLE reverie_meta_page (
       id serial primary key,
       page_serial integer not null,
       meta_data_id integer null references reverie_meta_data(id),
       meta_set_id integer null references reverie_meta_set(id)
);
