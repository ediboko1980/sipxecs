include $(top_srcdir)/config/utility.am

EXTRA_DIST = \
	$(initd_SCRIPTS:=.in) \
	$(bin_SCRIPTS:=.in) \
	$(cfmod_SCRIPTS:=.in) \
	$(libexec_SCRIPTS:=.in) \
	$(systemd_SCRIPTS:=.in)

libexecdir = @SIPX_LIBEXECDIR@
libexec_SCRIPTS = \
	sipx-config-maker \
	sipxlogwatcher-maker \
	sipx-allow-command

bin_SCRIPTS = \
	sipxagent \
	sipxagent-log-reader \
	sipxecs-setup \
	setup-master-host-domain \
	master-reconfig.js \
	sipx-archive \
	voicemail-cli
	
cfmoddir = @SIPX_CFMODULES@
cfmod_SCRIPTS = \
	sipx \
	readfile-if-exists \
	run-later \
	wait-on-log

initddir = @SIPX_SERVICEDIR@
initd_SCRIPTS = \
	sipxsupervisor \
	sipxlogwatcher \
	sipxecs

systemddir = /usr/lib/systemd/system
systemd_SCRIPTS = \
	sipxsupervisor.service

$(libexec_SCRIPTS) $(cfmod_SCRIPTS) $(initd_SCRIPTS) $(bin_SCRIPTS) $(systemd_SCRIPTS) : % : %.in Makefile
	@$(call SearchAndReplace,$<,$@)

CLEANFILES = $(bin_SCRIPTS) $(initd_SCRIPTS) $(libexec_SCRIPTS) $(cfmod_SCRIPTS) $(systemd_SCRIPTS)
