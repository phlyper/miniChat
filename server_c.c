#include <stdio.h> //autre
#include <string.h>
#include <stdlib.h>

#include <pthread.h> //thread
#include <unistd.h>

#include <sys/types.h> //socket
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define PORT 12345
#define MAX 20

pthread_mutex_t mutex;

typedef struct {
    char login[16];
    char pwd[16];
    int port;
    pthread_t thread;
    int socket;
} client;

client LCC[MAX];
int nbClient = 0;
int LastPort = 22200;

/*
char *copie_chaine(const char *source){
    char *chaine = malloc(strlen(source));
    if (chaine != NULL)
    {strcpy(chaine, source);
    }return chaine;
} */

char *getLogin(client c) {
    char *p = malloc(16 * sizeof (char));
    pthread_mutex_lock(&mutex);
    strcpy(p, c.login);
    pthread_mutex_unlock(&mutex);

    return p;
}

char *getPwd(client c) {
    char *p = malloc(16 * sizeof (char));
    pthread_mutex_lock(&mutex);
    strcpy(p, c.pwd);
    pthread_mutex_unlock(&mutex);

    return p;
}

int getPort(client c) {
    int p;
    pthread_mutex_lock(&mutex);
    p = c.port;
    pthread_mutex_unlock(&mutex);

    return p;
}

pthread_t getThread(client c) {
    pthread_t t;
    pthread_mutex_lock(&mutex);
    t = c.thread;
    pthread_mutex_unlock(&mutex);

    return t;
}

int getSocket(client c) {
    int s;
    pthread_mutex_lock(&mutex);
    s = c.socket;
    pthread_mutex_unlock(&mutex);

    return s;
}

char *getPortLogin(client c) {
    char *p = malloc(24 * sizeof (char));
    sprintf(p, "%d|%s", getPort(c), getLogin(c));
    printf("%s \n", p);

    return p;
}

void setLogin(client *c, char *p) {

    pthread_mutex_lock(&mutex);
    strcpy(c->login, p);
    pthread_mutex_unlock(&mutex);
}

void setPwd(client *c, char *p) {

    pthread_mutex_lock(&mutex);
    strcpy(c->pwd, p);
    pthread_mutex_unlock(&mutex);
}

void setPort(client *c, int p) {

    pthread_mutex_lock(&mutex);
    c->port = p;
    pthread_mutex_unlock(&mutex);
}

void setThread(client *c, pthread_t t) {

    pthread_mutex_lock(&mutex);
    c->thread = t;
    pthread_mutex_unlock(&mutex);
}

void setSocket(client *c, int s) {

    pthread_mutex_lock(&mutex);
    c->socket = s;
    pthread_mutex_unlock(&mutex);
}

void *traiter_requete(void* arg) {
    int x = (int) arg;
    char buffer[64];
    int i, existe = 0;

    printf("Connection avec un client est effectuee %d \n", x);

    while (1) {
    	strcpy(buffer, ""); // reintialiser le buffer
        int r = recv(getSocket(LCC[x]), buffer, sizeof (buffer), 0);
        if (r == -1) {
            printf("Erreur de reception\n");
        }

        /* decoupage du buffer '----|----|----' le separateur '|' */
        char p[3][64];
        strcpy(p[0], strtok(buffer, "|"));
        strcpy(p[1], strtok(NULL, "|"));
        strcpy(p[2], strtok(NULL, "|"));
        printf("buffer = [%s] ==> [%s] [%s] [%s] \n", buffer, p[0], p[1], p[2]);

        /* Inscription au serveur */
        /* buffer 'inscription|login|passwd' */
        if (strcmp(p[0], "inscription") == 0) {

            for (i = 0; i < nbClient; i++) {
                if (strcmp(p[1], getLogin(LCC[i])) == 0) {
                    existe = 1;
                }
            }
            if (existe == 1) {
                sprintf(buffer, "inscription|login_existant|%s", getLogin(LCC[x]));
                send(getSocket(LCC[x]), buffer, sizeof (buffer), 0);
                existe = 0;
                break;
            } else {
                setLogin(&LCC[x], p[1]);
                setPwd(&LCC[x], p[2]);

                sprintf(buffer, "inscription|ok|%s", getLogin(LCC[x]));
                send(getSocket(LCC[x]), buffer, sizeof (buffer), 0);
            }
        }

        /* Connexion au serveur */
        /* buffer 'connexion|login|passwd' */
        if (strcmp(p[0], "connexion") == 0) {
            if (strcmp(getLogin(LCC[x]), p[1]) == 0 && strcmp(getPwd(LCC[x]), p[2]) == 0) {
                setPort(&LCC[x], LastPort);
                LastPort++;

                sprintf(buffer, "connexion|ok|%s", getPortLogin(LCC[x]));
                send(getSocket(LCC[x]), buffer, sizeof (buffer), 0);
            } else {
                sprintf(buffer, "connexion|login_pwd_faux|%s", getPortLogin(LCC[x]));
                send(getSocket(LCC[x]), buffer, sizeof (buffer), 0);
            }
        }

        /* Actualisation du liste des client connectee 'LCC' */
        /* buffer 'update_LCC|----|----' */
        if (strcmp(p[0], "update_LCC") == 0) {
            printf("update LCC | %s", p[2]);

            sprintf(buffer, "update_LCC|debut|%d", nbClient);
            send(getSocket(LCC[x]), buffer, sizeof (buffer), 0);

            for (i = 0; i < nbClient; i++) {
                sprintf(buffer, "update_LCC|%d|%s", i, getPortLogin(LCC[i]));
                send(getSocket(LCC[x]), buffer, sizeof (buffer), 0);
            }

            sprintf(buffer, "update_LCC|fin|%d", nbClient);
            send(getSocket(LCC[x]), buffer, sizeof (buffer), 0);
        }

        /* Deconnexion du serveur */
        /* buffer 'deconnection|----|login' */
        if (strcmp(p[0], "deconnection") == 0) {
            printf("%s deconnecte ", p[2]);

            /* Fermeture de la socket */
            printf("Fermeture de la socket...\n");
            shutdown(getSocket(LCC[x]), 2);

            setLogin(&LCC[x], "");
            setPwd(&LCC[x], "");
            setPort(&LCC[x], 0);
            setSocket(&LCC[x], 0);

            printf("Client %s deconnecte\n", p[2]);
            break;
        }
    }
    pthread_exit(NULL);
}

int main(int agrn, char* arg[]) {

    int sock = socket(AF_INET, SOCK_STREAM, 0);

    struct sockaddr_in sin;
    struct sockaddr_in csin;
    int csock;
    socklen_t recsize = sizeof (csin);

    /* Si la socket est valide */
    if (sock != -1) {
        printf("La socket %d est maintenant ouverte en mode TCP/IP\n", sock);

        /* Configuration */
        sin.sin_addr.s_addr = htonl(INADDR_ANY); /* Adresse IP automatique */
        sin.sin_family = AF_INET; /* Protocole familial (IP) */
        sin.sin_port = htons(PORT); /* Listage du port */
        int sock_err = bind(sock, (struct sockaddr*) & sin, sizeof (sin));

        /* Si la socket fonctionne */
        if (sock_err != -1) {
            /* D�marrage du listage (mode server) */
            sock_err = listen(sock, MAX);
            printf("Listage du port %d...\n", PORT);

            /* Si la socket fonctionne */
            if (sock_err != -1) {
                /* Attente pendant laquelle le client se connecte */
                printf("Patientez pendant que le client se connecte sur le port %d...\n", PORT);

                while (1) {
                    /* Acceptation du connexion */
                    csock = accept(sock, (struct sockaddr*) & csin, &recsize);
                    printf("Un client se connecte avec la socket %d de %s|%d\n", csock, inet_ntoa(csin.sin_addr), htons(csin.sin_port));

                    setSocket(&LCC[nbClient], csock);
                    pthread_t t = getThread(LCC[nbClient]);
                    int th = pthread_create(&t, NULL, traiter_requete, (void *) nbClient);
                    if (th < 0)
                        printf("erreur de creation du thread");
                    else
                        nbClient++;
                }
            }
        }

        int i;
        for (i = 0; i < nbClient; i++) {
            pthread_join(getThread(LCC[i]), NULL);
        }
        /* Fermer la connexion (ferm�e dans les deux sens) */
        close(sock);
        printf("Fermeture du serveur terminee\n");
    }
    /* Detruire mutex */
    pthread_mutex_destroy(&mutex);

    return 0;
}
