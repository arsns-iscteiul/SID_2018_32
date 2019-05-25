# SID_2018_32

Grupo 32:
Ana Nunes 79086
Joaquim Rocha 78174
Leonardo Rosa 77755
Matias Correia 77624
Rita Caveirinha 77794
Rúben Silva 77586
Rodrigo Tavares 77761


Instruções para a mv:
Atenção: Este clone destina-se a ser corrido em VMWARE. 
Infelizmente a MV encontra-se configurada para o teclado Americano. Não conseguimos alterar esta
configuração mas iremos descrever como selecionar os caracteres necessários à execução deste guião.

Informações: 
Password do windows da máquina virtual: Passw0rd!

Passos: 

1-Abrir o XAMPP
2-Iniciar o servidor Apache e o servidor MySQL
(Caso tenha problemas no passo 2 deve abrir o Task Manager e fechar as instancias do mysqld.exe)
3-Abrir o cmd e correr o seguinte comando >>> cd Desktop >>> e depois 
>>> "C:\Program Files\Java\jdk1.8.0_211\bin\java" -jar ES_SID_32.jar >>>
( Para não ter problemas com os caracteres copie apenas o comando. As aspas fazem parte do comando!)
4- Este comando abre a aplicação. Para entrar como investigador deve usar a conta (user: matias@gmail.com pass:123)
Se pretender entrar como administrador (user: admin pass: admin).

Tenha apenas atenção ao testar as features que ao adicionar uma medição deve usar um inteiro em vez de um double para não dar conflito com a geração do gráfico.

Houve ainda um problema durante a exportação da base de dados para a MV fazendo com que o trigger que gera Alertas para medições manuais deixe de funcionar.
Para testar esta feature segue um guião a baixo.

1-No Xampp carregar no botão "Admin" à na aba mysql. 
2-Irá abrir um tab no browser. Aceda à base de dados com a conta (User: root pass: teste124)
3-Vá à tabela Medição e carregue no botão insert.
4-Insira manualmente valores que estejam fora do limite para uma variável_medida do investigador.
(Para o passo 4 é importante que o investigador tenha culturas e variáveis medidas para essas culturas.

