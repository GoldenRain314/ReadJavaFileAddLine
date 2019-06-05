import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.util.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisJava {

	private ParserFactory factory = getParserFactory();


	public void setLog(String fileName){
		//文件内容
		String content = readToString(fileName);
		//java文件内的方法及对应的行号
		List<String[]> lists = getFileInfoMap(content);
		for(String[] aaa : lists){
			Integer numb = readLineVarFile(fileName, aaa);
			if(numb == null){
				continue;
			}
			FileInsertRow.insertStringInFile(new File(fileName),numb + 1, "LoggingUtils.getEvoLogger().info(\"========" + numb + "=====" + aaa[1] +"=========\");");
			//method2("C:\\Users\\Administrator\\Desktop\\aaa.txt",fileName + "," + (numb +1) + "," + aaa[1] );
		}

	}
	public void method2(String file, String conent) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true)));
			out.write(conent+"\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> traverseFolder(String path , List<String> list) {
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (null == files || files.length == 0) {
				return list;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						traverseFolder(file2.getAbsolutePath(), list);
					} else {
						if(file2.getName().contains(".java")){
							list.add(file2.getAbsolutePath());
						}
					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
		return list;
	}

	public Integer readLineVarFile(String fileName, String[] f) {
		BufferedReader reader = null; //使用缓冲区的方法将数据读入到缓冲区中
		String line = "";
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			line = reader.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int num = 0;
		while (line != null) 	//当行数不为空时，输出该行内容及字符数
		{
			++num;
			if(f[0] != null && line.contains(f[0]) && line.contains(f[1]) && line.contains(f[2]) && line.contains("{")){
				return num;
			}
			if(line.contains(f[1]) && line.contains(f[2]) && line.contains("{")){
				return num;
			}

			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String readToString(String fileName) {
		String encoding = "UTF-8";
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support " + encoding);
			e.printStackTrace();
			return null;
		}
	}

	public List<String[]> getFileInfoMap(String content){
		Parser parser = factory.newParser(content, true, false, true);
		JCCompilationUnit unit = parser.parseCompilationUnit();
		List<String[]> list = new ArrayList<>();

		Scanner scanner = new Scanner();
		scanner.visitCompilationUnit(unit, null);
		List<MethodTree> methodTrees = scanner.getMethodTrees();
		for(MethodTree methodTree : methodTrees) {
			try {
				String methodName = "";
				if(methodTree.getBody() == null){
					continue;
				}
				String[] s = new String[3];
				if (null != methodTree.getReturnType()) {
					s[0] = methodTree.getReturnType().toString();
					s[1] = methodTree.getName().toString();
					s[2] = methodTree.getParameters().toString();
				} else {
					s[0] = "";
					s[1] = methodTree.getName().toString();
					s[2] = methodTree.getParameters().toString();
				}
					list.add(s);

			} catch (Exception e) {
				System.err.println("函数解析失败");
				e.printStackTrace();
			}
		}
		return list;
	}
	public static class Scanner extends TreeScanner<List<Object>, List<Object>> {
		private List<ImportTree> importTrees = new ArrayList<ImportTree>();
		private List<MethodTree> methodTrees = new ArrayList<MethodTree>();
		private List<TypeParameterTree> typeParameterTrees = new ArrayList<TypeParameterTree>();
		private List<AnnotationTree> annotationTrees = new ArrayList<AnnotationTree>();
		private List<InstanceOfTree> instanceOfTrees = new ArrayList<InstanceOfTree>();
		private List<LabeledStatementTree> labeledStatementTrees = new ArrayList<LabeledStatementTree>();
		private List<MemberSelectTree> memberSelectTrees = new ArrayList<MemberSelectTree>();
		private List<MethodInvocationTree> methodInvocationTrees = new ArrayList<MethodInvocationTree>();
		private List<Tree> trees = new ArrayList<Tree>();
		private List<ParameterizedTypeTree> parameterizedTypeTrees = new ArrayList<ParameterizedTypeTree>();
		private List<PrimitiveTypeTree> primitiveTypeTrees = new ArrayList<PrimitiveTypeTree>();
		private List<TypeCastTree> typeCastTrees = new ArrayList<TypeCastTree>();
		private List<VariableTree> variableTrees = new ArrayList<VariableTree>();

		@Override
		public List<Object> visitImport(ImportTree arg0, List<Object> arg1) {
			importTrees.add(arg0);
			return super.visitImport(arg0, arg1);
		}
		@Override
		public List<Object> visitMethod(MethodTree arg0, List<Object> arg1) {
			methodTrees.add(arg0);
			return super.visitMethod(arg0, arg1);
		}
		@Override
		public List<Object> visitTypeParameter(TypeParameterTree arg0, List<Object> arg1) {
			typeParameterTrees.add(arg0);
			return super.visitTypeParameter(arg0, arg1);
		}
		@Override
		public List<Object> visitAnnotation(AnnotationTree arg0, List<Object> arg1) {
			annotationTrees.add(arg0);
			return super.visitAnnotation(arg0, arg1);
		}
		@Override
		public List<Object> visitInstanceOf(InstanceOfTree arg0, List<Object> arg1) {
			instanceOfTrees.add(arg0);
			return super.visitInstanceOf(arg0, arg1);
		}
		@Override
		public List<Object> visitLabeledStatement(LabeledStatementTree arg0, List<Object> arg1) {
			labeledStatementTrees.add(arg0);
			return super.visitLabeledStatement(arg0, arg1);
		}
		@Override
		public List<Object> visitMemberSelect(MemberSelectTree arg0, List<Object> arg1) {
			memberSelectTrees.add(arg0);
			return super.visitMemberSelect(arg0, arg1);
		}
		@Override
		public List<Object> visitMethodInvocation(MethodInvocationTree arg0, List<Object> arg1) {
			methodInvocationTrees.add(arg0);
			return super.visitMethodInvocation(arg0, arg1);
		}
		@Override
		public List<Object> visitOther(Tree arg0, List<Object> arg1) {
			trees.add(arg0);
			return super.visitOther(arg0, arg1);
		}
		@Override
		public List<Object> visitParameterizedType(ParameterizedTypeTree arg0, List<Object> arg1) {
			parameterizedTypeTrees.add(arg0);
			return super.visitParameterizedType(arg0, arg1);
		}
		@Override
		public List<Object> visitPrimitiveType(PrimitiveTypeTree arg0, List<Object> arg1) {
			primitiveTypeTrees.add(arg0);
			return super.visitPrimitiveType(arg0, arg1);
		}
		@Override
		public List<Object> visitTypeCast(TypeCastTree arg0, List<Object> arg1) {
			typeCastTrees.add(arg0);
			return super.visitTypeCast(arg0, arg1);
		}
		@Override
		public List<Object> visitVariable(VariableTree arg0, List<Object> arg1) {
			variableTrees.add(arg0);
			return super.visitVariable(arg0, arg1);
		}

		public List<ImportTree> getImportTrees() {
			return importTrees;
		}
		public void setImportTrees(List<ImportTree> importTrees) {
			this.importTrees = importTrees;
		}
		public List<MethodTree> getMethodTrees() {
			return methodTrees;
		}

		public void setMethodTrees(List<MethodTree> methodTrees) {
			this.methodTrees = methodTrees;
		}
		public List<TypeParameterTree> getTypeParameterTrees() {
			return typeParameterTrees;
		}
		public void setTypeParameterTrees(List<TypeParameterTree> typeParameterTrees) {
			this.typeParameterTrees = typeParameterTrees;
		}
		public List<AnnotationTree> getAnnotationTrees() {
			return annotationTrees;
		}
		public void setAnnotationTrees(List<AnnotationTree> annotationTrees) {
			this.annotationTrees = annotationTrees;
		}
		public List<InstanceOfTree> getInstanceOfTrees() {
			return instanceOfTrees;
		}
		public void setInstanceOfTrees(List<InstanceOfTree> instanceOfTrees) {
			this.instanceOfTrees = instanceOfTrees;
		}
		public List<LabeledStatementTree> getLabeledStatementTrees() {
			return labeledStatementTrees;
		}
		public void setLabeledStatementTrees(List<LabeledStatementTree> labeledStatementTrees) {
			this.labeledStatementTrees = labeledStatementTrees;
		}
		public List<MemberSelectTree> getMemberSelectTrees() {
			return memberSelectTrees;
		}
		public void setMemberSelectTrees(List<MemberSelectTree> memberSelectTrees) {
			this.memberSelectTrees = memberSelectTrees;
		}
		public List<MethodInvocationTree> getMethodInvocationTrees() {
			return methodInvocationTrees;
		}
		public void setMethodInvocationTrees(List<MethodInvocationTree> methodInvocationTrees) {
			this.methodInvocationTrees = methodInvocationTrees;
		}
		public List<Tree> getTrees() {
			return trees;
		}
		public void setTrees(List<Tree> trees) {
			this.trees = trees;
		}
		public List<ParameterizedTypeTree> getParameterizedTypeTrees() {
			return parameterizedTypeTrees;
		}
		public void setParameterizedTypeTrees(List<ParameterizedTypeTree> parameterizedTypeTrees) {
			this.parameterizedTypeTrees = parameterizedTypeTrees;
		}
		public List<PrimitiveTypeTree> getPrimitiveTypeTrees() {
			return primitiveTypeTrees;
		}
		public void setPrimitiveTypeTrees(List<PrimitiveTypeTree> primitiveTypeTrees) {
			this.primitiveTypeTrees = primitiveTypeTrees;
		}
		public List<TypeCastTree> getTypeCastTrees() {
			return typeCastTrees;
		}
		public void setTypeCastTrees(List<TypeCastTree> typeCastTrees) {
			this.typeCastTrees = typeCastTrees;
		}
		public List<VariableTree> getVariableTrees() {
			return variableTrees;
		}
		public void setVariableTrees(List<VariableTree> variableTrees) {
			this.variableTrees = variableTrees;
		}
	}
	private static ParserFactory getParserFactory() {
		Context context = new Context();
		JavacFileManager.preRegister(context);
		ParserFactory factory = ParserFactory.instance(context);
		return factory;
	}

} 