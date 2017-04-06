package crypto;

import java.math.BigInteger;

public class RSAcrypto {

	public static BigInteger encryptBlock(BigInteger mes, RSAsaveKEY key, boolean priv){
		if(priv){
			return mes.modPow(key.getPrivateExponent(), key.getModulus());
		}else{
			return mes.modPow(key.getPublicExponent(), key.getModulus());
		}
	}
}
