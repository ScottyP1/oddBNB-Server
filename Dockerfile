FROM ubuntu:latest
LABEL authors="scotty_p"

ENTRYPOINT ["top", "-b"]