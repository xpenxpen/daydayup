package org.xpen.hello.dl.djl.stablediffusion;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;

/**
 * 控制扩散去噪的时间步和每一步 latent 的更新
 *
 */
public class PndmScheduler {

    private static final int TRAIN_TIMESTEPS = 1000;
    private static final float BETA_START = 0.00085f;
    private static final float BETA_END = 0.012f;

    private NDManager manager;
    private NDArray alphasCumProd;
    private float finalAlphaCumProd;
    private int counter;
    private NDArray curSample;
    private NDList ets;
    private int stepSize;
    private int[] timesteps;

    public PndmScheduler(NDManager manager) {
        this.manager = manager;
        NDArray betas =
                manager.linspace(
                                (float) Math.sqrt(BETA_START),
                                (float) Math.sqrt(BETA_END),
                                TRAIN_TIMESTEPS)
                        .square();
        NDArray alphas = manager.ones(betas.getShape()).sub(betas);
        alphasCumProd = alphas.cumProd(0);
        finalAlphaCumProd = alphasCumProd.get(0).toFloatArray()[0];
        ets = new NDList();
    }

    public NDArray addNoise(NDArray latent, NDArray noise, int timesteps) {
    	//图生图时用
        float alphaProd = alphasCumProd.get(timesteps).toFloatArray()[0];
        float sqrtOneMinusAlphaProd = (float) Math.sqrt(1 - alphaProd);
    	//把随机噪声按某个时间步的噪声强度混入 latent
        latent = latent.mul(alphaProd).add(noise.mul(sqrtOneMinusAlphaProd));
        return latent;
    }

    public void initTimesteps(int inferenceSteps, int offset) {
    	//把训练时的 1000 步，映射到推理时用户指定的 inferenceSteps，然后反转顺序
        stepSize = TRAIN_TIMESTEPS / inferenceSteps;
        NDArray timestepsNd = manager.arange(0, inferenceSteps).mul(stepSize).add(offset);

        // np.concatenate([self._timesteps[:-1], self._timesteps[-2:-1],
        // self._timesteps[-1:]])[::-1]
        NDArray part1 = timestepsNd.get(new NDIndex(":-1"));
        NDArray part2 = timestepsNd.get(new NDIndex("-2:-1"));
        NDArray part3 = timestepsNd.get(new NDIndex("-1:"));
        NDList list = new NDList();
        list.add(part1);
        list.add(part2);
        list.add(part3);
        // timesteps = timesteps.get(new NDIndex("::-1"));
        timesteps = NDArrays.concat(list).flatten().flip(0).toIntArray();
    }

    public NDArray step(NDArray modelOutput, int timestep, NDArray sample) {
        int prevTimestep = timestep - stepSize;
        if (counter != 1) {
            ets.add(modelOutput);
        } else {
            prevTimestep = timestep;
            timestep -= stepSize;
        }
        /*
         * 它会缓存历史几步的 modelOutput 到 ets，然后根据历史长度使用不同阶数的公式：
         * 1个历史值：初始处理
         * 2个历史值：二阶
         * 3个历史值：三阶
         * 4个及以上：四阶
         * 本质上是在做一种多步数值积分近似，属于 PNDM 的核心思想
         */

        if (ets.size() == 1 && counter == 0) {
            curSample = sample;
        } else if (ets.size() == 1 && counter == 1) {
            modelOutput = modelOutput.add(ets.get(0)).div(2);
            sample = curSample;
        } else if (ets.size() == 2) {
            NDArray firstModel = ets.get(ets.size() - 1).mul(3);
            NDArray secondModel = ets.get(ets.size() - 2).mul(-1);
            modelOutput = firstModel.add(secondModel);
            modelOutput = modelOutput.div(2);
        } else if (ets.size() == 3) {
            NDArray firstModel = ets.get(ets.size() - 1).mul(23);
            NDArray secondModel = ets.get(ets.size() - 2).mul(-16);
            NDArray thirdModel = ets.get(ets.size() - 3).mul(5);
            modelOutput = firstModel.add(secondModel).add(thirdModel);
            modelOutput = modelOutput.div(12);
        } else {
            NDArray firstModel = ets.get(ets.size() - 1).mul(55);
            NDArray secondModel = ets.get(ets.size() - 2).mul(-59);
            NDArray thirdModel = ets.get(ets.size() - 3).mul(37);
            NDArray fourthModel = ets.get(ets.size() - 4).mul(-9);
            modelOutput = firstModel.add(secondModel).add(thirdModel).add(fourthModel);
            modelOutput = modelOutput.div(24);
        }

        NDArray prevSample = getPrevSample(sample, timestep, prevTimestep, modelOutput);
        prevSample.setName("prev_sample");
        counter++;

        return prevSample;
    }

    public int[] getTimesteps() {
        return timesteps;
    }

    public void setTimesteps(int[] timesteps) {
        this.timesteps = timesteps;
    }

    private NDArray getPrevSample(NDArray sample, int timestep, int prevTimestep, NDArray modelOutput) {
    	//从 t 走到 t-1 的实际数学公式实现
        float alphaProdT = alphasCumProd.toFloatArray()[timestep];
        float alphaProdTPrev;

        if (prevTimestep >= 0) {
            alphaProdTPrev = alphasCumProd.toFloatArray()[prevTimestep];
        } else {
            alphaProdTPrev = finalAlphaCumProd;
        }

        float betaProdT = 1 - alphaProdT;
        float betaProdTPrev = 1 - alphaProdTPrev;

        float sampleCoeff = (float) Math.sqrt(alphaProdTPrev / alphaProdT);
        float modelOutputCoeff =
                alphaProdT * (float) Math.sqrt(betaProdTPrev)
                        + (float) Math.sqrt(alphaProdT * betaProdT * alphaProdTPrev);

        sample = sample.mul(sampleCoeff);
        modelOutput = modelOutput.mul(alphaProdTPrev - alphaProdT).div(modelOutputCoeff).neg();
        return sample.add(modelOutput);
    }
}
