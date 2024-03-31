
CREATE TABLE public.profile (
    id bigint NOT NULL,
    active boolean,
    name character varying(255),
    roles character varying(255),
    creation_date timestamp(6) with time zone,
    deleted_date timestamp(6) with time zone,
    updated_date timestamp(6) with time zone
);

CREATE SEQUENCE public.profile_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.profile_id_seq OWNED BY public.profile.id;


CREATE TABLE public."user" (
    id bigint NOT NULL,
    active boolean,
    
    username character varying(255),
    email character varying(255),
    email_activation boolean,
    password character varying(255),
    avatar varchar(255),
    google_id varchar(250),
    facebook_id varchar(250),
    date_derniere_connexion timestamp(6) with time zone,
    
    activation_token character varying(255),
    identity_token character varying(255),
    identity_token_date_end timestamp(6) with time zone,
    connection_trials int,
    date_derniere_tentative timestamp,

    creation_date timestamp(6) with time zone,
    deleted_date timestamp(6) with time zone,
    updated_date timestamp(6) with time zone
);

CREATE SEQUENCE public.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.user_id_seq OWNED BY public."user".id;


CREATE TABLE public.user_profile (
    user_id bigint NOT NULL,
    profile_id bigint NOT NULL
);
ALTER TABLE ONLY public.profile ALTER COLUMN id SET DEFAULT nextval('public.profile_id_seq'::regclass);
ALTER TABLE ONLY public."user" ALTER COLUMN id SET DEFAULT nextval('public.user_id_seq'::regclass);

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public."user"
    ADD CONSTRAINT uk_sb8bbouer5wak8vyiiy4pf2bx UNIQUE (username);
ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.user_profile
    ADD CONSTRAINT fkqcd5nmg7d7ement27tt9sf3bi FOREIGN KEY (user_id) REFERENCES public."user"(id);
ALTER TABLE ONLY public.user_profile
    ADD CONSTRAINT fkqfbftbxicceqbmvj87g9be2qn FOREIGN KEY (profile_id) REFERENCES public.profile(id);
    
create table news (
    id bigserial not null,
    active boolean,
    user_id bigint,
    title varchar(255),
    image varchar(255),
    avatar varchar(255),
    description text,
    resume text,
    tags varchar(255),
    creation_date timestamp(6) with time zone,
    deleted_date timestamp(6) with time zone,
    updated_date timestamp(6) with time zone,
    primary key (id)
);

alter table if exists news add constraint FK4538gbwfa03nwr9edl3fdloo9 foreign key (user_id) references public.user;

create table "email-queue" (
    id bigserial not null, 
    active boolean, 
    email varchar(255), 
    subject varchar(255), 
    user_id bigint, 
    is_send boolean, 
    is_error boolean, 
    text TEXT, 
    creation_date timestamp(6), 
    deleted_date timestamp(6), 
    updated_date timestamp(6), 
    primary key (id)
);

create table "notification" (
    id bigserial not null,
    active boolean,
    user_id bigint,
    read boolean,
    super_type varchar(255),
    category varchar(255),
    type varchar(255),
    title varchar(255),
    message varchar(255),
    url varchar(255),
    entity_id bigint,
    users varchar(255),
    creation_date timestamp(6),
    deleted_date timestamp(6),
    updated_date timestamp(6),
    primary key (id)
);

alter table if exists public.notification add constraint FKb0yvoep4h4k92ipon31wmdf7e foreign key (user_id) references public.user;

create table notification_token (
    id bigserial not null, 
    active boolean, 
    creation_date timestamp(6), 
    deleted_date timestamp(6), 
    updated_date timestamp(6), 
    token varchar(255), 
    user_id bigint, 
    primary key (id)
);

alter table if exists public.notification_token add constraint FK4rrd7saug5k6u08xqcu5lawcy foreign key (user_id) references public.user;

create table "user-payment" (
    id bigserial not null, 
    active boolean, 
    user_id bigint, 
    amount bigint, 
    amountReceived bigint, 
    currency varchar(255), 
    intent varchar(255), 
    paymentId varchar(255), 
    paymentMethod varchar(255), 
    secret varchar(255), 
    source varchar(255), 
    status varchar(255), 
    creation_date timestamp(6), 
    deleted_date timestamp(6), 
    updated_date timestamp(6), 
    primary key (id)
);

alter table if exists "user-payment" add constraint FKt8pnb50djdwcruf33kbws6kx4 foreign key (user_id) references public.user;
