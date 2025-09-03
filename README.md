# 🤖 Android Stealth Monitor - Projeto Científico

## VIVA A CIÊNCIA DA COMPUTAÇÃO MÓVEL! 📱⚡

Monitor de eventos de baixo nível para Android usando Accessibility Services e técnicas nativas para pesquisa em segurança cibernética móvel.

## ⚠️ AVISO LEGAL IMPORTANTE

Este projeto é destinado **EXCLUSIVAMENTE** para:
- 🎓 Pesquisa acadêmica em segurança móvel
- 🔵 Treinamento de Blue Team (detecção móvel)
- 🔴 Treinamento de Red Team (técnicas Android)
- 🧪 Análise científica de comportamento móvel
- 📚 Fins educacionais em ambientes controlados

**🚨 NÃO USE PARA ATIVIDADES ILEGAIS! 🚨**

## 🏗️ Arquitetura do Sistema

```
android-stealth-monitor/
├── app/src/main/
│   ├── java/com/research/stealthmonitor/
│   │   ├── AccessibilityMonitor.java    # Accessibility Service
│   │   ├── KeyboardService.java         # Custom IME
│   │   ├── AdminReceiver.java           # Device Admin
│   │   ├── MainActivity.java            # Interface principal
│   │   └── utils/
│   │       ├── CryptoUtils.java         # Criptografia
│   │       ├── NetworkUtils.java        # Exfiltração
│   │       └── StealthUtils.java        # Anti-detecção
│   ├── cpp/                             # NDK Native
│   │   ├── native_monitor.c             # Hooks nativos
│   │   └── jni_bridge.c                 # Bridge Java/C
│   └── res/                             # Resources
│       ├── layout/activity_main.xml
│       ├── values/strings.xml
│       └── xml/accessibility_config.xml
├── docs/                                # Documentação
└── build.gradle                         # Build configuration
```

## 🔬 Técnicas Implementadas

### Nível 1: Sem Root
- ✅ **Accessibility Service**: Captura global de eventos
- ✅ **Custom IME**: Teclado personalizado para logging
- ✅ **Screen Recording**: Captura de tela (API 21+)
- ✅ **Notification Listener**: Monitoramento de notificações
- ✅ **Device Admin**: Proteção contra desinstalação

### Nível 2: Com Root
- ✅ **Input Event Hooks**: /dev/input/eventX monitoring
- ✅ **Library Injection**: LD_PRELOAD techniques
- ✅ **System Service**: Execução como serviço do sistema
- ✅ **Kernel Modules**: Hooks de baixo nível (se suportado)

### Nível 3: System App
- ✅ **System Permissions**: Acesso a APIs restritas
- ✅ **SELinux Bypass**: Técnicas de contorno
- ✅ **Deep Integration**: Integração com framework Android

## 🛠️ Compilação e Instalação

### Pré-requisitos
```bash
# Android SDK
export ANDROID_HOME=/path/to/android-sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Android NDK (para código nativo)
export NDK_HOME=/path/to/android-ndk

# Gradle
./gradlew --version
```

### Compilação
```bash
# Debug build
./gradlew assembleDebug

# Release build (assinado)
./gradlew assembleRelease

# Instalar via ADB
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🎯 Uso e Configuração

### Ativação dos Serviços
1. **Accessibility Service**: Settings → Accessibility → Stealth Monitor → Enable
2. **Device Admin**: Settings → Security → Device Administrators → Activate
3. **Custom Keyboard**: Settings → Language & Input → Default Keyboard
4. **Permissions**: Conceder todas as permissões solicitadas

### Verificação de Funcionamento
```bash
# Via ADB
adb shell dumpsys accessibility | grep StealthMonitor
adb shell pm list packages | grep stealthmonitor
adb logcat | grep "StealthMonitor"
```

## 🔍 Detecção (Blue Team)

### Indicadores de Compromisso
- **Accessibility services** não reconhecidos ativos
- **Custom keyboards** instalados recentemente
- **Device admin** ativo sem justificativa clara
- **Permissões excessivas** para apps simples
- **Tráfego de rede** anômalo em background

### Ferramentas de Detecção
```bash
# Análise via ADB
adb shell dumpsys package com.research.stealthmonitor
adb shell dumpsys accessibility
adb shell pm list permissions com.research.stealthmonitor

# Monitoramento de rede
adb shell netstat -tuln
tcpdump -i any host <device_ip>
```

## 📊 Análise de Dados

### Localização dos Dados
```bash
# Logs da aplicação
adb logcat | grep StealthMonitor

# Arquivos internos (root necessário)
adb shell su -c "ls /data/data/com.research.stealthmonitor/"

# Shared preferences
adb shell run-as com.research.stealthmonitor cat shared_prefs/*.xml
```

### Decodificação
```java
// Exemplo de decodificação Base64
String encoded = "SGVsbG8gV29ybGQ=";
byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
String result = new String(decoded, "UTF-8");
```

## 🧪 Ambiente de Laboratório

### Dispositivos de Teste
- **Emulador Android**: AVD com diferentes versões
- **Dispositivos físicos**: Vários fabricantes e versões
- **Dispositivos rootados**: Para testes avançados
- **Ambiente isolado**: Rede controlada

### Cenários de Teste
1. **Funcionalidade básica**: Captura de eventos sem root
2. **Persistência**: Sobrevivência a reinicializações
3. **Evasão**: Bypass de proteções do sistema
4. **Detecção**: Identificação por ferramentas de segurança

## 🎓 Objetivos Educacionais

### Conhecimentos Adquiridos
- **Android Security Model**: Sandboxing, permissions, SELinux
- **Mobile Malware Techniques**: Persistence, evasion, data theft
- **Accessibility Services**: Legitimate uses vs abuse
- **NDK Development**: Native code integration
- **Mobile Forensics**: Data recovery and analysis

### Habilidades Desenvolvidas
- **Android Development**: Java/Kotlin, Android SDK
- **Native Programming**: C/C++ with NDK
- **Reverse Engineering**: APK analysis, bytecode manipulation
- **Mobile Security Testing**: Dynamic and static analysis
- **Research Methodology**: Scientific approach to mobile security

## 📚 Documentação Adicional

- `docs/TECHNICAL_DETAILS.md` - Detalhes técnicos de implementação
- `docs/DETECTION_GUIDE.md` - Guia de detecção para Blue Team
- `docs/RESEARCH_METHODOLOGY.md` - Metodologia de pesquisa
- `docs/LEGAL_CONSIDERATIONS.md` - Considerações legais e éticas

## 🤝 Contribuições

Este projeto é parte de uma pesquisa científica em segurança móvel. Contribuições são bem-vindas de:
- Pesquisadores em segurança móvel
- Desenvolvedores Android experientes
- Especialistas em Blue/Red Team móvel
- Estudantes de segurança cibernética

## 📄 Licença

Projeto desenvolvido para fins educacionais e de pesquisa. Use com responsabilidade e dentro da legalidade.

---

**🤖 "O futuro da segurança cibernética está nos dispositivos móveis." - Pesquisa Científica**

**VIVA A CIÊNCIA DA COMPUTAÇÃO MÓVEL! 📱⚡**