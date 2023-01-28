--
-- PostgreSQL database dump
--

-- Dumped from database version 14.4
-- Dumped by pg_dump version 14.4

-- Started on 2023-01-11 23:16:11 IST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: cloudsqlsuperuser
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO cloudsqlsuperuser;

--
-- TOC entry 3955 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: cloudsqlsuperuser
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 209 (class 1259 OID 16465)
-- Name: amazonasin; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.amazonasin (
    asin character varying(20) NOT NULL,
    title character varying(200),
    expectedlength character varying(10),
    expectedwidth character varying(10),
    expectedheight character varying(10),
    expectedweight character varying(10),
    obtainedlength character varying(10),
    obtainedwidth character varying(10),
    obtainedheight character varying(10),
    obtainedweight character varying(10),
    lengthmismatch boolean,
    widthmismatch boolean,
    heightmismatch boolean,
    weightmismatch boolean,
    lastpulled character varying(20),
    buybox boolean DEFAULT false,
    sendmail boolean DEFAULT false,
    fbafee character varying(10) DEFAULT NULL::character varying,
    storagefee character varying(10) DEFAULT NULL::character varying,
    sellingprice character varying(10) DEFAULT NULL::character varying,
    productcost character varying(10) DEFAULT NULL::character varying,
    shipping character varying(10) DEFAULT NULL::character varying,
    profit character varying(10) DEFAULT NULL::character varying,
    profitpercentage character varying(10) DEFAULT NULL::character varying,
    profitstatus boolean DEFAULT true,
    dimensionjson character varying(2000) DEFAULT NULL::character varying,
    fbalastpulled character varying(20) DEFAULT NULL::character varying,
    active boolean DEFAULT true,
    totalreviews integer DEFAULT 0,
    newreviews integer DEFAULT 0,
    ratings integer DEFAULT 0,
    incomingunits text DEFAULT '0'::text,
    maillengthmismatch boolean DEFAULT true,
    mailwidthmismatch boolean DEFAULT true,
    mailheightmismatch boolean DEFAULT true,
    mailweightmismatch boolean DEFAULT true,
    ubc text DEFAULT (0 / 0),
    availableunits character varying DEFAULT '0'::character varying,
    ref character varying(20) DEFAULT 0
);


ALTER TABLE public.amazonasin OWNER TO postgres;

--
-- TOC entry 3956 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: cloudsqlsuperuser
--

REVOKE ALL ON SCHEMA public FROM cloudsqladmin;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO cloudsqlsuperuser;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2023-01-11 23:16:14 IST

--
-- PostgreSQL database dump complete
--

