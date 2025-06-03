# Localization Files

Multi-language support for ShadowDefenderMC plugin messages.

## Supported Languages

### Currently Available
- `en_US.yml` - English (United States) - Default
- `en_GB.yml` - English (United Kingdom)
- `es_ES.yml` - Spanish (Spain)
- `fr_FR.yml` - French (France)
- `de_DE.yml` - German (Germany)
- `it_IT.yml` - Italian (Italy)
- `pt_BR.yml` - Portuguese (Brazil)
- `ru_RU.yml` - Russian (Russia)
- `zh_CN.yml` - Chinese (Simplified)
- `ja_JP.yml` - Japanese (Japan)
- `ko_KR.yml` - Korean (South Korea)
- `pl_PL.yml` - Polish (Poland)
- `nl_NL.yml` - Dutch (Netherlands)
- `sv_SE.yml` - Swedish (Sweden)
- `da_DK.yml` - Danish (Denmark)
- `no_NO.yml` - Norwegian (Norway)
- `fi_FI.yml` - Finnish (Finland)
- `tr_TR.yml` - Turkish (Turkey)
- `ar_SA.yml` - Arabic (Saudi Arabia)
- `he_IL.yml` - Hebrew (Israel)

## Configuration
Set your preferred language in the main config.yml:
```yaml
language: "en_US"  # Default language
fallback_language: "en_US"  # Fallback if translation missing
```

## Message Categories

### Protection Messages
- Attack detection notifications
- Rate limit warnings
- Bot detection alerts
- VPN/Proxy blocking messages

### Player Messages
- Challenge mode instructions
- Verification prompts
- Error messages
- Success confirmations

### Admin Messages
- Status reports
- Configuration reload confirmations
- Debug information
- Statistics summaries

## Contributing Translations
1. Copy `en_US.yml` as a template
2. Translate all message values (keep keys unchanged)
3. Test with your server
4. Submit via GitHub Issues or Pull Request

## Message Formatting
- Use `{player}` for player names
- Use `{ip}` for IP addresses
- Use `{count}` for numbers
- Use `{time}` for time values
- Use color codes: `&c` (red), `&a` (green), `&e` (yellow), etc.

---
**ShadowDefenderMC** - Created by HexLordDev