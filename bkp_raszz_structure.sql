--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.15
-- Dumped by pg_dump version 9.5.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: bicraszzgit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bicraszzgit (
    linenumber bigint,
    path character varying,
    content character varying,
    project character varying,
    szz_date timestamp without time zone,
    copypath character varying,
    copyrevision bigint,
    mergerev boolean,
    branchrev boolean,
    changeproperty boolean,
    missed boolean,
    furtherback boolean,
    contentdiff character varying,
    issample boolean,
    diffjmessage character varying,
    diffjlocation character varying,
    refdiffindex integer,
    adjustmentindex integer,
    indexposrefac integer,
    indexfurtherback integer,
    indexchangepath integer,
    isrefac boolean,
    revision character varying,
    fixrevision character varying,
    islargest boolean,
    islatest boolean,
    startrevision character varying,
    startpath character varying,
    startlinenumber bigint,
    startcontent character varying,
    isvalidfix boolean,
    hasmatchmaszz boolean,
    isvalidresult boolean
);


ALTER TABLE public.bicraszzgit OWNER TO postgres;

--
-- Name: callerrefdiff; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.callerrefdiff (
    revision character varying,
    project character varying,
    summary character varying,
    entityafter character varying,
    callermethod character varying,
    callerpath character varying,
    callerstartline bigint,
    callerendline bigint,
    refactoringtype character varying,
    simplename character varying,
    callerline bigint,
    revisiontype character varying,
    issample boolean,
    nestinglevel bigint,
    type character varying,
    tool character varying
);


ALTER TABLE public.callerrefdiff OWNER TO postgres;

--
-- Name: linkedissuegit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.linkedissuegit (
    project character varying,
    bug_id character varying,
    buggy_revision character varying,
    fixed_revision character varying,
    fixed_revision_2 character varying,
    bic_revision character varying,
    obs1 character varying,
    obs2 character varying
);


ALTER TABLE public.linkedissuegit OWNER TO postgres;

--
-- Name: refdiffresult; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.refdiffresult (
    revision character varying,
    project character varying,
    summary character varying,
    refactoringtype character varying,
    entitybefore character varying,
    entityafter character varying,
    elementtype character varying,
    callers bigint,
    afterstartline bigint,
    afterendline bigint,
    afterpathfile character varying,
    beforestartline bigint,
    beforeendline bigint,
    beforepathfile character varying,
    aftersimplename character varying,
    aftercontent text,
    afterstartscope bigint,
    revisiontype character varying,
    issample boolean,
    afternestinglevel bigint,
    beforestartscope bigint,
    beforesimplename character varying,
    beforecontent text,
    beforenestinglevel bigint,
    tool character varying
);


ALTER TABLE public.refdiffresult OWNER TO postgres;

--
-- Name: szz_refac_revisionprocessed; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.szz_refac_revisionprocessed (
    revision character varying,
    project character varying,
    tool character varying
);


ALTER TABLE public.szz_refac_revisionprocessed OWNER TO postgres;

--
-- Name: INDEX_PROJ_CALLER_REFAC; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "INDEX_PROJ_CALLER_REFAC" ON public.callerrefdiff USING btree (project);


--
-- Name: INDEX_PROJ_REFAC; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "INDEX_PROJ_REFAC" ON public.refdiffresult USING btree (project);


--
-- Name: INDEX_REV_CALLER_REFAC; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "INDEX_REV_CALLER_REFAC" ON public.callerrefdiff USING btree (revision);


--
-- Name: INDEX_REV_REFDIFF; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "INDEX_REV_REFDIFF" ON public.refdiffresult USING btree (revision);


--
-- Name: bicraszzgit_log; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER bicraszzgit_log BEFORE INSERT OR DELETE OR UPDATE ON public.bicraszzgit FOR EACH ROW EXECUTE PROCEDURE public.log_tables_reverse_engineering();


--
-- Name: callerrefdiff_log; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER callerrefdiff_log BEFORE INSERT OR DELETE OR UPDATE ON public.callerrefdiff FOR EACH ROW EXECUTE PROCEDURE public.log_tables_reverse_engineering();


--
-- Name: linkedissuegit_log; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER linkedissuegit_log BEFORE INSERT OR DELETE OR UPDATE ON public.linkedissuegit FOR EACH ROW EXECUTE PROCEDURE public.log_tables_reverse_engineering();


--
-- Name: refdiffresult_log; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER refdiffresult_log BEFORE INSERT OR DELETE OR UPDATE ON public.refdiffresult FOR EACH ROW EXECUTE PROCEDURE public.log_tables_reverse_engineering();


--
-- Name: szz_refac_revisionprocessed_log; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER szz_refac_revisionprocessed_log BEFORE INSERT OR DELETE OR UPDATE ON public.szz_refac_revisionprocessed FOR EACH ROW EXECUTE PROCEDURE public.log_tables_reverse_engineering();


--
-- Name: TABLE bicraszzgit; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.bicraszzgit FROM PUBLIC;
REVOKE ALL ON TABLE public.bicraszzgit FROM postgres;
GRANT ALL ON TABLE public.bicraszzgit TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.bicraszzgit TO "ra-szz";


--
-- Name: TABLE callerrefdiff; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.callerrefdiff FROM PUBLIC;
REVOKE ALL ON TABLE public.callerrefdiff FROM postgres;
GRANT ALL ON TABLE public.callerrefdiff TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.callerrefdiff TO "ra-szz";


--
-- Name: TABLE linkedissuegit; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.linkedissuegit FROM PUBLIC;
REVOKE ALL ON TABLE public.linkedissuegit FROM postgres;
GRANT ALL ON TABLE public.linkedissuegit TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.linkedissuegit TO "ra-szz";


--
-- Name: TABLE refdiffresult; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.refdiffresult FROM PUBLIC;
REVOKE ALL ON TABLE public.refdiffresult FROM postgres;
GRANT ALL ON TABLE public.refdiffresult TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.refdiffresult TO "ra-szz";


--
-- Name: TABLE szz_refac_revisionprocessed; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.szz_refac_revisionprocessed FROM PUBLIC;
REVOKE ALL ON TABLE public.szz_refac_revisionprocessed FROM postgres;
GRANT ALL ON TABLE public.szz_refac_revisionprocessed TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.szz_refac_revisionprocessed TO "ra-szz";


--
-- PostgreSQL database dump complete
--

