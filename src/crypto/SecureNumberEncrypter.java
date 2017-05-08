package crypto;

import java.security.NoSuchAlgorithmException;

public class SecureNumberEncrypter extends NumberEncrypter{

	private byte[] firstSubKey;
	private byte[] secondSubKey;
	
	public SecureNumberEncrypter(byte[] pw) {
		super(pw, 16);
		
		byte[] opad = new byte[pw.length];
		byte[] ipad = new byte[pw.length];
		for (int i = 0; i < pw.length; i++) {
			opad[i] = (byte)(0x5c ^ pw[i]);
			ipad[i] = (byte)(0x36 ^ pw[i]);
		}
		
		try {
			SCMHA s = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			s.update(ipad);
			firstSubKey = s.digest();
			s = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			s.update(opad);
			secondSubKey = s.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public final byte[] encryptAdvanced(byte[] b){
		printCode(b);
		xOr(b, firstSubKey);
		printCode(b);
		b = shuffle(b, false, 100);
		printCode(b);
		b = encrypt(b);
		printCode(b);
		xOr(b, secondSubKey);
		printCode(b);
		return b;
	}
	
	public final byte[] decryptAdvanced(byte[] b){
		xOr(b, secondSubKey);
		b = decrypt(b);
		b = shuffle(b, true, 100);
		xOr(b, firstSubKey);
		return b;
	}
	
	private void xOr(byte[] b, byte[] x){
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte)(b[i] ^ x[i]);
		}
	}
	
	private byte[] shuffle(byte[] code, boolean decrypt, int s){
		if(s%2 == 1){
			byte[] b = new byte[code.length];
			int j = s%code.length;
			for (int i = 0; i < code.length; i++) {
				if(decrypt)
					b[i] = code[j];
				else
					b[j] = code[i];
					
				j++;
				if(j>=code.length) j = 0;
			}
			return b;
		}else{
			byte[] b = new byte[code.length];
			int j = s%code.length;
			for (int i = 0; i < code.length; i++) {
				if(decrypt)
					b[i] = code[code.length-j-1];
				else
					b[code.length-j-1] = code[i];
					
				j++;
				if(j>=code.length) j = 0;
			}
			return b;
		}
	}
	
	private void printCode(byte[] code){
		//System.out.println(new String(code));
		
		for (int i = 0; i < code.length; i++) {
			System.out.print(code[i]+", ");
		}
		System.out.println(":"+code.length);
	}

}
