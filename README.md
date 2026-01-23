forked from [ringosham/TranslateMod](https://github.com/ringosham/TranslateMod)

# Real time translation mod 1.8.9

A Minecraft Forge mod that translates the chat to the language you desire.

## Disclaimer

Translation services are provided by Google. 

## Translation engine

This uses both the Google translator API (Free, kind of) and the Cloud translation API (Paid).

You do not need to configure anything to use the free option.

The free translation mirror service is provided by [Tencent Cloud EdgeOne](https://edgeone.ai/).

For the paid option, follow these steps.
1. Create a new project in [Google Cloud Platform](https://console.cloud.google.com)
2. Add your payment methods in the billing section
3. In "APIs & Services > Library", enable "Cloud Translation API"
4. In "APIs & Services > Credentials", create an API key via "create credentials"
5. This is your API key. Copy it and keep it secret as this is linked to your payment account.
6. Restrict your API key. Click on restrict key on API restrictions and only allow "Cloud Translation API".
7. Copy the key to the "API key" section in the mod and save

## Download

Go to [Releases](https://github.com/Guation/TranslateMod/releases)

## Bug reporting

Please report any bugs in issues. Do not ask how to get this mod working on xxx servers. Please send a PM in Curseforge instead. Those issues will be closed without question.

## License

All code in this mod, except dependencies, or otherwise specified, are under GPL version 3.0