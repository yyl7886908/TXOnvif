#onvif ¹¤³ÌmakefileÄ¿Â¼
PROJDIR = .
OBJSDIR = $(PROJDIR)/objects
INCSDIR = $(PROJDIR)/source/include 

SRCS  += $(wildcard $(PROJDIR)/source/*.c)
OBJS  := $(addprefix $(OBJSDIR)/, $(addsuffix .o, $(basename $(notdir $(SRCS)))))
INCS  += -I$(INCSDIR)

CC = gcc -DDEBUG

CFLAGS  += 
#CFLAGS  += -O2 
CFLAGS  += -I$(INCSDIR)

#LDFLAGS += -ldl -lm -lpthread

EXENAME = txonviftest 
DEPFILE = deps

.PHONY:all
all: $(DEPFILE)  $(EXENAME) 

$(DEPFILE): $(SRCS) Makefile
	@echo "make onvif test ...";
	@-rm -f $(DEPFILE)
	@for f in $(SRCS); do \
		OBJ=$(OBJSDIR)/`basename $$f|sed  -e 's/\.c/\.o/'`; \
		echo $$OBJ: $$f >> $(DEPFILE); \
		echo '	$(CC) $$(CFLAGS) -c -o $$@ $$^'>> $(DEPFILE); \
		done

-include $(DEPFILE)
$(EXENAME): $(OBJS)
	$(CC) $(CFLAGS)  $(OBJS) -o "$@"  $(LDFLAGS)

.PHONY:clean
clean:
	rm -fr $(OBJSDIR)/*.o
	rm -fr $(DEPFILE)
	rm -fr $(EXENAME)
