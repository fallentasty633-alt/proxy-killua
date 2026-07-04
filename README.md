# Shizuku Injector - FF Config

Este projeto está configurado para compilar automaticamente via **GitHub Actions**.

## Como gerar o seu APK:

1.  **Crie um novo repositório** no seu GitHub (ex: `ff-injector`).
2.  **Suba todos os arquivos** desta pasta para o repositório.
3.  O GitHub detectará automaticamente o arquivo em `.github/workflows/android.yml` e iniciará a compilação.
4.  Para ver o progresso e baixar o APK:
    *   Vá na aba **"Actions"** no seu repositório no GitHub.
    *   Clique no workflow que está rodando (geralmente chamado "Android CI").
    *   Quando terminar (ficar verde), role até a seção **"Artifacts"**.
    *   Lá você encontrará o arquivo `app-debug`, que contém o seu **APK pronto para instalar**.

## Detalhes do App:
- **Login**: Chave `KILLUA`
- **Cores**: Branco e Azul
- **Destino**: `/Android/data/com.dts.freefireth/files/localconfig.json`
- **Requisito**: Shizuku ativo no celular.
