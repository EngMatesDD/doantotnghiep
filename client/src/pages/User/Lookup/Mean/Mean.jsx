import classNames from 'classnames/bind';
import { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCaretDown, faCaretUp } from '@fortawesome/free-solid-svg-icons';

import styles from './Mean.module.scss';
import Example from '../Example';

const cx = classNames.bind(styles);

function Mean({ isOpenAllMean, mean, index }) {
    const [isOpenVietnamese, setIsOpenVietnamese] = useState(isOpenAllMean);
    useEffect(() => {
        setIsOpenVietnamese(isOpenAllMean);
    }, [isOpenAllMean]);

    const toggle = () => {
        setIsOpenVietnamese(!isOpenVietnamese);
    };

    return (
        <div className={cx('m-3 my-6 justify-center')} key={index}>
            {mean.level !== '' && <span className={cx('rounded-full bg-blue-100 p-3')}>{mean.level}</span>}
            <span className={cx(' font-bold')}> {mean.conceptEnglish} </span>
            <button onClick={() => toggle()} className=" hover:rounded-lg hover:bg-blue-100">
                <FontAwesomeIcon icon={faCaretUp} className={`mx-3 text-lg ${isOpenVietnamese ? 'hidden' : ''}`} />
                <FontAwesomeIcon icon={faCaretDown} className={`mx-3 text-lg ${isOpenVietnamese ? '' : 'hidden'}`} />
            </button>
            <br />
            <span
                className={cx(
                    `bg-yellow-100 p-1  italic ${isOpenVietnamese ? '' : 'hidden'} ${mean.level !== '' ? 'mx-20' : ''}`,
                )}
            >
                {mean.conceptVietnamese}
            </span>
            <div className={cx('flex flex-col gap-5 p-6')}>
                {mean.examples.map((example, index) => {
                    return <Example key={index} isOpenAllMean={isOpenAllMean} example={example}></Example>;
                })}
            </div>
        </div>
    );
}
export default Mean;
